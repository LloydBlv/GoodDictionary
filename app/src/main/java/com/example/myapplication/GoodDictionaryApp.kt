package com.example.myapplication

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.dictionarysync.DataSyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import timber.log.Timber

@HiltAndroidApp
class GoodDictionaryApp : Application(), Configuration.Provider {

  @Inject
  lateinit var workerFactory: HiltWorkerFactory

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()

  override fun onCreate() {
    super.onCreate()
    Timber.plant(Timber.DebugTree())
    DataSyncWorker.start(this)
  }
}
