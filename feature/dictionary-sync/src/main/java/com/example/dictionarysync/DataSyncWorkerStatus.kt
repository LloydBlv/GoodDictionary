package com.example.dictionarysync

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

interface DataSyncWorkerStatus {
  fun getWorkerStatus(): Flow<WorkInfo?>
  fun retrySync()
}