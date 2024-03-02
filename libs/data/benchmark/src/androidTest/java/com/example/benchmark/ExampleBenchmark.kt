package com.example.benchmark

import android.app.Application
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.database.AppDatabase
import com.example.data.DictionaryInsertDefault
import com.example.data.database.WordEntity
import com.example.data.database.WordsDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private lateinit var database: AppDatabase
    private lateinit var wordsDao: WordsDao

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        wordsDao = database.wordDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    private fun createInMemoryDb(): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }
    private fun createDiskDb(): AppDatabase {
        return Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
            "words_database"
        ).allowMainThreadQueries().build()
    }
    @Test
    fun memoryDb_Dao_no_chunk() {
        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { createInMemoryDb() }
            val syncDictionary = DictionaryInsertDefault(database)
            syncDictionary.insertUsingDao(createWordsSequence())
        }
    }
    @Test
    fun diskDb_Dao_no_chunk() {
        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { createDiskDb() }
            val syncDictionary = DictionaryInsertDefault(database)
            syncDictionary.insertUsingDao(createWordsSequence())
        }
    }
    @Test
    fun memoryDb_sqlite_no_chunk() {
        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { createInMemoryDb() }
            val syncDictionary = DictionaryInsertDefault(database)
            syncDictionary.insertUsingSqlite(createWordsSequence())
        }
    }
    @Test
    fun memoryDb_sqlite_1000_chunk() {
        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { createInMemoryDb() }
            val syncDictionary = DictionaryInsertDefault(database, chunkSize = 1000)
            syncDictionary.insertUsingSqlite(createWordsSequence())
        }
    }
    @Test
    fun memoryDb_sqlite_10_000_chunk() {
        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { createInMemoryDb() }
            val syncDictionary = DictionaryInsertDefault(database, chunkSize = 10_000)
            syncDictionary.insertUsingSqlite(createWordsSequence())
        }
    }
    @Test
    fun diskDb_sqlite_no_chunk() {
        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { createInMemoryDb() }
            val syncDictionary = DictionaryInsertDefault(database)
            syncDictionary.insertUsingSqlite(createWordsSequence())
        }
    }

    @Test
    fun chunk1000_with_sqlite() {
        val words = createWordsSequence()

        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            ).allowMainThreadQueries().build() }

            val db = database.openHelper.writableDatabase
            db.beginTransaction()

            words.chunked(10_000)
                .forEach {
                    val values = ContentValues()
                    it.forEach { values.put("word", it) }
                    db.insert("words", CONFLICT_REPLACE, values)
                }
            db.setTransactionSuccessful()
            db.endTransaction()
        }
        runBlocking { assert(wordsDao.getAllWords().size == 370150) }
    }

    fun chunk2000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = createWordsSequence()

        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            ).allowMainThreadQueries().build() }

            val wordsDao = runWithTimingDisabled { database.wordDao() }
            words.chunked(2000)
                .forEach {
                    wordsDao.insert(it.map { WordEntity(word = it) })
                }
        }
    }

    fun chunk3000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = createWordsSequence()

        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            ).allowMainThreadQueries().build() }

            val wordsDao = runWithTimingDisabled { database.wordDao() }
            words.chunked(3000)
                .forEach {
                    wordsDao.insert(it.map { WordEntity(word = it) })
                }
        }
    }

    fun chunk4000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = createWordsSequence()

        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            ).allowMainThreadQueries().build() }

            val wordsDao = runWithTimingDisabled { database.wordDao() }
            words.chunked(4000)
                .forEach {
                    wordsDao.insert(it.map { WordEntity(word = it) })
                }
        }
    }

    fun chunk5000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = createWordsSequence()

        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            ).allowMainThreadQueries().build() }

            val wordsDao = runWithTimingDisabled { database.wordDao() }
            words.chunked(5000)
                .forEach {
                    wordsDao.insert(it.map { WordEntity(word = it) })
                }
        }
    }

    fun chunk6000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = createWordsSequence()

        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            ).allowMainThreadQueries().build() }

            val wordsDao = runWithTimingDisabled { database.wordDao() }
            words.chunked(6000)
                .forEach {
                    wordsDao.insert(it.map { WordEntity(word = it) })
                }
        }
    }

    fun chunk7000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = createWordsSequence()

        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            ).allowMainThreadQueries().build() }

            val wordsDao = runWithTimingDisabled { database.wordDao() }
            words.chunked(7000)
                .forEach {
                    wordsDao.insert(it.map { WordEntity(word = it) })
                }
        }
    }

    fun chunk8000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = createWordsSequence()

        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            ).allowMainThreadQueries().build() }

            val wordsDao = runWithTimingDisabled { database.wordDao() }
            words.chunked(8000)
                .forEach {
                    wordsDao.insert(it.map { WordEntity(word = it) })
                }
        }
    }

    fun chunk250() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = createWordsSequence()

        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            ).allowMainThreadQueries().build() }

            val wordsDao = runWithTimingDisabled { database.wordDao() }
            words.chunked(250)
                .forEach {
                    wordsDao.insert(it.map { WordEntity(word = it) })
                }
        }
    }

    fun chunk500() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = createWordsSequence()

        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            ).allowMainThreadQueries().build() }

            val wordsDao = runWithTimingDisabled { database.wordDao() }
            words.chunked(500)
                .forEach {
                    wordsDao.insert(it.map { WordEntity(word = it) })
                }
        }
    }

    private fun createWordsSequence() = (1..370150).map { "$it" }.asSequence()
}