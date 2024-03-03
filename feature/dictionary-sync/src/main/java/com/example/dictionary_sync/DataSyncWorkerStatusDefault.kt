package com.example.dictionary_sync

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataSyncWorkerStatusDefault @Inject constructor(
    @ApplicationContext private val context: Context
): DataSyncWorkerStatus {
    override fun getWorkerStatus(): Flow<WorkInfo> {
        return WorkManager.getInstance(context)
            .getWorkInfoByIdFlow(DataSyncWorker.ID)
    }
}