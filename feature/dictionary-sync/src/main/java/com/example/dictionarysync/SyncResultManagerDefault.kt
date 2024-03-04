package com.example.dictionarysync

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sync-result")
val SYNC_SUCCESS = booleanPreferencesKey("sync_successful")

class SyncResultManagerDefault @Inject constructor(
  @ApplicationContext private val context: Context,
) : SyncResultManager {

  override suspend fun markSyncSuccess() {
    context.dataStore.edit { settings ->
      settings[SYNC_SUCCESS] = true
    }
  }

  override fun isFinished(): Boolean {
    return runBlocking { context.dataStore.data.first()[SYNC_SUCCESS] == true }
  }
}
