package com.example.data

interface SyncDictionary {
    fun insertUsingSqlite(words: Sequence<String>)
    fun insertUsingDao(words: Sequence<String>)
}