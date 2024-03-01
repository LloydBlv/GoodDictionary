package com.example.data

import android.util.Log
import com.example.domain.DictionaryLoader
import com.example.domain.DictionaryState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlin.system.measureTimeMillis

class DictionaryRepositoryDefault @Inject constructor(
    private val dao: WordsDao,
    private val syncDictionary: SyncDictionary,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : DictionaryLoader {
    override fun load(): Flow<DictionaryState> {
        return flow {
            emit(DictionaryState.Loading)
            val count = dao.getCount()
            if (count > 0) {
                emit(DictionaryState.Loaded(count))
                return@flow
            }
            emit(DictionaryState.ParsingItems)

            val time = measureTimeMillis {
                downloadAndInsertWords("https://raw.githubusercontent.com/dwyl/english-words/master/words_alpha.txt")
//                context.assets.open("words_alpha3.txt")
//                    .bufferedReader()
//                    .useLines {
//                        syncDictionary.insertUsingSqlite(it.sorted())
//                    }
            }
            Log.e("dictionaryParsing", "took ${time}ms to parse items")
            emit(DictionaryState.Loaded(dao.getCount()))
        }.flowOn(coroutineDispatcher)
    }

    private suspend fun downloadAndInsertWords(urlString: String) = withContext(coroutineDispatcher) {
        val url = URL(urlString)
        val connection = url.openConnection()
        connection.connect()

        val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
        reader.useLines {
            syncDictionary.insertUsingSqlite(it)
        }
    }

    private suspend fun downloadAndInsertWords(
        urlString: String,
        progressCallback:suspend (Int) -> Unit
    ) = withContext(coroutineDispatcher) {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        val totalSize = connection.contentLength // Total size of the file
        var downloadedSize = 0 // Size of data downloaded so far
        val batch = mutableListOf<String>()
//        val batch = mutableListOf<WordEntity>()
        val batchSize = 1000 // Adjust based on performance testing
        var itemCount = 0 // To track the number of items processed
        val estimatedTotalItems = 370150
        connection.inputStream.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                val lines = reader.lineSequence().iterator()

                while (lines.hasNext()) {
                    val line = lines.next()
                    batch.add(line)
                    downloadedSize += line.toByteArray().size // Update downloaded size approximation
                    itemCount++

                    if (batch.size >= batchSize) {
                        syncDictionary.insertUsingSqlite(batch.asSequence())
//                        dao.insertAll(batch)
                        batch.clear() // Clear the batch after insertion

                        // Update progress callback
                        val progress =
                            (itemCount * 100 / estimatedTotalItems) // Estimate or calculate total items
                        progressCallback(progress)
                    }
                }

                // Insert any remaining items in the batch
                if (batch.isNotEmpty()) {
                    syncDictionary.insertUsingSqlite(batch.asSequence())
//                    dao.insertAll(batch)
                    // Final progress update
                    progressCallback(100) // Assuming completion
                }
            }
        }
    }
}