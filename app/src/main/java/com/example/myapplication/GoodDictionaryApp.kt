package com.example.myapplication

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.dictionary_sync.DataSyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GoodDictionaryApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory : HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        DataSyncWorker.initWorker(this)
    }
}