package com.example.dictionary_sync

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import androidx.work.WorkInfo.State as WorManagerState
import com.example.dictionary_sync.DictionarySyncStateWatcher.State as DictionaryState

class DictionarySyncStateWatcherDefault @Inject constructor(
    private val statusProvider: DataSyncWorkerStatus
) : DictionarySyncStateWatcher {
    override fun watch(): Flow<DictionaryState> {
        return statusProvider
            .getWorkerStatus()
            .map(::toDictionaryState)
    }

    override fun retry() {
        statusProvider.retrySync()
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