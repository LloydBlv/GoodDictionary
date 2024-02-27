package com.example.data

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File

class DictionaryLoaderDefault (
    private val chunkSize: Int = 1000,
    private val dispatcher: CoroutineDispatcher
): DictionaryLoader {


    override suspend fun insertWords(words: Sequence<String>, dao: WordsDao) {
        words.chunked(500)
            .forEach {
                dao.insertAll(it.map { WordEntity(word = it) })
            }
    }

    override fun load(context: Context) {
        context.assets.list("/")?.onEach {
            println("asset=$it")
        }
        println("assets=${context.assets.open("words_alpha3.txt").available()}")
    }
    override suspend fun insertWords(filePath: String, dao: WordsDao) {
        insertWords(File(filePath).bufferedReader(), dao)
    }

    override suspend fun insertWords(bufferedReader: BufferedReader, dao: WordsDao) = withContext(dispatcher) {
        bufferedReader.useLines {
            insertWords(it, dao)
        }
    }
}