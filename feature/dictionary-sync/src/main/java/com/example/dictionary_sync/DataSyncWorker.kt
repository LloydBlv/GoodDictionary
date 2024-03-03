package com.example.dictionary_sync

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.domain.usecases.SyncDictionaryRecordsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException
import java.util.UUID
import kotlin.system.measureTimeMillis

private const val Progress = "Progress"
private const val DICTIONARY_DOWNLOAD_URL =
    "https://raw.githubusercontent.com/dwyl/english-words/master/words_alpha.txt"

@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val syncDictionaryRecordsUseCase: SyncDictionaryRecordsUseCase,
    private val syncResultManager: SyncResultManager,
) : CoroutineWorker(appContext, params) {
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "11"
        private const val NOTIFICATION_CHANNEL_NAME = "Work Service"
        val ID: UUID = UUID.nameUUIDFromBytes("sync-worker".toByteArray())
        const val FAILURE_MESSAGE_DATA = "failure_message_data"
        fun start(context: Context) {
            Timber.tag("worker").e("initWorker")
            val request = OneTimeWorkRequestBuilder<DataSyncWorker>()
                .setId(ID)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork("DataSyncWorkerName", ExistingWorkPolicy.REPLACE, request)
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo()
    }


    override suspend fun doWork(): Result {
        try {
            if (syncResultManager.isFinished()) {
                setProgress(workDataOf(Progress to 100))
                return Result.success()
            }
            val timeMs = measureTimeMillis {
                downloadAndInsertWords(urlString = DICTIONARY_DOWNLOAD_URL, progressCallback = {
                    Timber.e("progress=$it")
                    setProgress(workDataOf(Progress to it))
                })
            }
            Timber.e("took " + timeMs + "ms to insert records")
            syncResultManager.markSyncSuccess()
        } catch (ex: Exception) {
            Timber.e(ex, "While doWork")
            return when {
                ex is UnknownHostException -> {
                    Result.failure(workDataOf(FAILURE_MESSAGE_DATA to "Please check your internet connection"))
                }
                runAttemptCount > 3 -> {
                    Result.failure(workDataOf(FAILURE_MESSAGE_DATA to ex.localizedMessage))
                }
                else -> {
                    Result.retry()
                }
            }
        }
        return Result.success()
    }

    private suspend fun downloadAndInsertWords(
        urlString: String,
        progressCallback: suspend (Int) -> Unit
    ) = withContext(Dispatchers.IO) {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.contentLength // Total size of the file
        var downloadedSize = 0 // Size of data downloaded so far
        val batch = mutableListOf<String>()
        val batchSize = 1000 // Adjust based on performance testing
        var itemCount = 0 // To track the number of items processed
        val estimatedTotalItems = 370_150
        connection.inputStream.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                val lines = reader.lineSequence().iterator()

                while (lines.hasNext()) {
                    val line = lines.next()
                    batch.add(line)
                    downloadedSize += line.toByteArray().size // Update downloaded size approximation
                    itemCount++

                    if (batch.size >= batchSize) {
                        syncDictionaryRecordsUseCase.invoke(batch)
                        batch.clear() // Clear the batch after insertion
                        val progress =
                            (itemCount * 100 / estimatedTotalItems) // Estimate or calculate total items
                        progressCallback(progress)
                    }
                }
                if (batch.isNotEmpty()) {
                    syncDictionaryRecordsUseCase.invoke(batch)
                    progressCallback(100) // Assuming completion
                }
            }
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    Intent("applicationContext, MainActivity::class.java"),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentTitle("Good dictionary")
            .setLocalOnly(true)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setContentText("Syncing dictionary...")
            .build()
        return ForegroundInfo(1337, notification)
    }

}

