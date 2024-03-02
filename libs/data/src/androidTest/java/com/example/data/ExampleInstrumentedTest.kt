package com.example.data

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.database.AppDatabase
import com.example.data.database.WordsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
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
    fun useAppContext() = runBlocking {

        val dictionaryLoader = DictionaryLoaderDefault(dispatcher = Dispatchers.IO)
        val app = ApplicationProvider.getApplicationContext<Application>()
        app.assets.use {
            val reader = it.open("words_alpha2.txt").bufferedReader()
            dictionaryLoader.insertWords(reader, dao = wordsDao)
            assertEquals(wordsDao.getAllWords().size, 370105)
        }


    }
}