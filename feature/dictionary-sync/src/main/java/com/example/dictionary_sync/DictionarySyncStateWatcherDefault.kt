package com.example.dictionary_sync

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import androidx.work.WorkInfo.State as WorManagerState
import com.example.dictionary_sync.DictionarySyncStateWatcher.State as DictionaryState

class DictionarySyncStateWatcherDefault @Inject constructor(
    @ApplicationContext private val context: Context
) : DictionarySyncStateWatcher {
    override fun watch(): Flow<DictionaryState> {
        return WorkManager.getInstance(context)
            .getWorkInfoByIdFlow(DataSyncWorker.ID)
            .map(::toDictionaryState)
    }

    private fun toDictionaryState(workInfo: WorkInfo?) = when (workInfo?.state) {
        WorManagerState.ENQUEUED -> DictionaryState.Loading
        WorManagerState.RUNNING ->
            DictionaryState.Progress(workInfo.progress.getInt("Progress", 0))

        WorManagerState.SUCCEEDED -> DictionaryState.Loaded
        WorManagerState.FAILED -> DictionaryState.Failure(
            workInfo.outputData.getString(
                DataSyncWorker.FAILURE_MESSAGE_DATA
            )
        )

        WorManagerState.BLOCKED -> DictionaryState.Blocked
        WorManagerState.CANCELLED -> DictionaryState.Cancelled
        else -> DictionaryState.Unknown
    }
}