package com.example.data.sync

import com.example.domain.DataSyncWorkerStarter
import com.example.domain.DictionarySyncStateWatcher
import com.example.domain.SyncResultManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataSyncModule {
  @Binds
  fun bindSyncResult(result: SyncResultManagerDefault): SyncResultManager

  @Binds
  fun bindStateWatcher(watcherDefault: DictionarySyncStateWatcherDefault): DictionarySyncStateWatcher

  @Binds
  fun bindSyncStatus(status: DataSyncWorkerStatusDefault): DataSyncWorkerStatus

  @Binds
  fun bindWorkerStarter(default: DataSyncWorkerStarterDefault): DataSyncWorkerStarter
}
