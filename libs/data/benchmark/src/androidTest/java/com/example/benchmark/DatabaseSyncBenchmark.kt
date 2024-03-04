package com.example.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.DictionaryInsertDefault
import com.example.data.database.AppDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(Parameterized::class)
class DatabaseSyncBenchmark(
    val insertMethod: InsertMethod,
    val inMemory: Boolean,
    val chunkSize: Int?
) {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private fun createInMemoryDb(): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
          .fallbackToDestructiveMigration()
          .build()
    }

    private fun createDiskDb(): AppDatabase {
        return Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
            "words_database"
        ).allowMainThreadQueries()
          .fallbackToDestructiveMigration()
          .build()
    }

    private fun createDb(): AppDatabase {
        return if (inMemory) {
            createInMemoryDb()
        } else {
            createDiskDb()
        }
    }
    @Test()
    fun benchmarkWordsInsertion() {
        benchmarkRule.measureRepeated {
            val database = runWithTimingDisabled { createDb() }
            val syncDictionary = DictionaryInsertDefault(database).apply {
                chunkSize = this@DatabaseSyncBenchmark.chunkSize
            }
            when (insertMethod) {
                InsertMethod.DAO -> syncDictionary.insertUsingDao(createWordsSequence())
                InsertMethod.SQLITE -> syncDictionary.insertUsingSqlite(createWordsSequence())
            }
        }
    }

    private fun createWordsSequence() = (1..100).map { "$it" }.asSequence()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: method={0}, memory={1}, chunk={2}")
        fun testCase(): List<Array<out Any?>> {
            val result = mutableListOf<Array<Any?>>()
            result.add(arrayOf(InsertMethod.DAO, true, null))
            result.add(arrayOf(InsertMethod.DAO, false, null))

            result.add(arrayOf(InsertMethod.SQLITE, true, null))
            result.add(arrayOf(InsertMethod.SQLITE, false, null))

            result.add(arrayOf(InsertMethod.DAO, true, 500))
            result.add(arrayOf(InsertMethod.DAO, false, 500))

            result.add(arrayOf(InsertMethod.SQLITE, true, 500))
            result.add(arrayOf(InsertMethod.SQLITE, false, 500))
            for (index in 1000..10_000 step 1_000) {
                result.add(arrayOf(InsertMethod.DAO, true, index))
                result.add(arrayOf(InsertMethod.DAO, false, index))
                result.add(arrayOf(InsertMethod.SQLITE, true, index))
                result.add(arrayOf(InsertMethod.SQLITE, false, index))
            }
            return result
        }
    }
}
enum class InsertMethod {
    DAO,
    SQLITE
}
