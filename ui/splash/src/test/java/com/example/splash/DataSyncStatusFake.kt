package com.example.splash

import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.workDataOf
import com.example.dictionary_sync.DataSyncWorker
import com.example.dictionary_sync.DataSyncWorkerStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class DataSyncStatusFake : DataSyncWorkerStatus {
    private var status = MutableStateFlow<WorkInfo?>(null)
    suspend fun emit(
        state: WorkInfo.State, tags: Set<String> = emptySet(),
        progress: Int? = null,
        outputData: Data = Data.EMPTY
    ) {
        status.emit(
            WorkInfo(
                id = DataSyncWorker.ID,
                state = state,
                tags = tags,
                outputData = outputData,
                progress = progress?.let { workDataOf("Progress" to progress) } ?: Data.EMPTY
            )
        )
    }

    override fun getWorkerStatus(): Flow<WorkInfo?> {
        return status
    }
}