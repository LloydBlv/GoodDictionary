package com.example.data

import android.content.Context
import java.io.BufferedReader

interface DictionaryLoader {
    fun load(context: Context)
    suspend fun insertWords(filePath: String, dao: WordsDao)
    suspend fun insertWords(words: Sequence<String>, dao: WordsDao)
    suspend fun insertWords(bufferedReader: BufferedReader, dao: WordsDao)
}