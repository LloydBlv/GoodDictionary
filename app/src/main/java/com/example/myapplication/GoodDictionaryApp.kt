package com.example.myapplication

import android.app.Application
import android.os.Build
import android.os.StrictMode
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.domain.DataSyncWorkerStarter
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import timber.log.Timber

@HiltAndroidApp
class GoodDictionaryApp : Application(), Configuration.Provider {

  @Inject
  lateinit var workerFactory: HiltWorkerFactory

  @Inject
  lateinit var dataSyncWorkerStarter: DataSyncWorkerStarter

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
      setupStrictMode()
    }
    dataSyncWorkerStarter.start()
  }

  private fun setupStrictMode() {
    StrictMode.setThreadPolicy(
      StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build(),
    )
    StrictMode.setVmPolicy(
      StrictMode.VmPolicy.Builder()
        .detectLeakedSqlLiteObjects()
        .detectActivityLeaks()
        .detectLeakedClosableObjects()
        .detectLeakedRegistrationObjects()
        .detectFileUriExposure()
        .detectCleartextNetwork()
        .apply {
          if (Build.VERSION.SDK_INT >= 26) {
            detectContentUriWithoutPermission()
          }
          if (Build.VERSION.SDK_INT >= 29) {
            detectCredentialProtectedWhileLocked()
          }
          if (Build.VERSION.SDK_INT >= 31) {
            detectIncorrectContextUse()
            detectUnsafeIntentLaunch()
          }
        }
        .penaltyLog()
        .build(),
    )
  }
}
