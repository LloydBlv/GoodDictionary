package com.example.domain


interface DictionaryLoader {
    suspend fun sync(records: List<String>)
}