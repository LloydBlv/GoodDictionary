package com.example.dictionary_sync

interface SyncResultManager {
    fun isFinished(): Boolean
    suspend fun markSyncSuccess()
}