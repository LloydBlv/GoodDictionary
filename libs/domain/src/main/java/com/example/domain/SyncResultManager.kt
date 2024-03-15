package com.example.domain

interface SyncResultManager {
  fun isFinished(): Boolean
  suspend fun markSyncSuccess()
}
