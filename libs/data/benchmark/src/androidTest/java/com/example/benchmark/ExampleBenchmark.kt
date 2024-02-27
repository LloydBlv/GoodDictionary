package com.example.benchmark

import android.app.Application
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.AppDatabase
import com.example.data.DictionaryLoaderDefault
import com.example.data.WordEntity
import com.example.data.WordsDao
import kotlinx.coroutines.Dispatchers
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

    @Test
    fun chunk1000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = (1..370150).map { "$it" }.asSequence()

        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            ).allowMainThreadQueries().build() }

            val wordsDao = runWithTimingDisabled { database.wordDao() }
            words.chunked(1000)
                .forEach {
                    wordsDao.insert(it.map { WordEntity(word = it) })
                }
        }
    }
    @Test
    fun chunk2000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = (1..370150).map { "$it" }.asSequence()

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
    @Test
    fun chunk3000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = (1..370150).map { "$it" }.asSequence()

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
    @Test
    fun chunk4000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = (1..370150).map { "$it" }.asSequence()

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
    @Test
    fun chunk5000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = (1..370150).map { "$it" }.asSequence()

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
    @Test
    fun chunk6000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = (1..370150).map { "$it" }.asSequence()

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
    @Test
    fun chunk7000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = (1..370150).map { "$it" }.asSequence()

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
    @Test
    fun chunk8000() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = (1..370150).map { "$it" }.asSequence()

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
    @Test
    fun chunk250() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = (1..370150).map { "$it" }.asSequence()

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
    @Test
    fun chunk500() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val words = (1..370150).map { "$it" }.asSequence()

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
}