package com.example.dictionarysync

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DataSyncWorkerStatusDefault @Inject constructor(
  @ApplicationContext private val context: Context,
) : DataSyncWorkerStatus {
  override fun getWorkerStatus(): Flow<WorkInfo> {
    return WorkManager.getInstance(context)
      .getWorkInfoByIdFlow(DataSyncWorker.ID)
  }

  override fun retrySync() {
    DataSyncWorker.start(context)
  }
}
