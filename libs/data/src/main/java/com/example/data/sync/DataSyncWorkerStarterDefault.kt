package com.example.data.sync

import android.content.Context
import com.example.domain.DataSyncWorkerStarter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DataSyncWorkerStarterDefault @Inject constructor(
  @ApplicationContext private val context: Context,
): DataSyncWorkerStarter {
  override fun start() {
    DataSyncWorker.start(context)
  }
}
