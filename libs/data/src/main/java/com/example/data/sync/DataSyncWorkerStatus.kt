package com.example.data.sync

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

interface DataSyncWorkerStatus {
  fun getWorkerStatus(): Flow<WorkInfo?>
  fun retrySync()
}
