package com.example.dictionarysync

interface SyncResultManager {
  fun isFinished(): Boolean
  suspend fun markSyncSuccess()
}
