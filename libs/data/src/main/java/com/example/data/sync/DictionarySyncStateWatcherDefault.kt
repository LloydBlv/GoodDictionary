package com.example.data.sync

import androidx.work.WorkInfo
import androidx.work.WorkInfo.State as WorManagerState
import com.example.domain.DictionarySyncStateWatcher.State as DictionaryState
import com.example.domain.DictionarySyncStateWatcher
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DictionarySyncStateWatcherDefault @Inject constructor(
  private val statusProvider: DataSyncWorkerStatus,
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
        DataSyncWorker.FAILURE_MESSAGE_DATA,
      ),
    )

    WorManagerState.BLOCKED -> DictionaryState.Blocked
    WorManagerState.CANCELLED -> DictionaryState.Cancelled
    else -> DictionaryState.Unknown
  }
}
