package com.example.words_list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.hasSize
import com.example.data.AppDatabase
import com.example.data.SyncDictionaryDefault
import com.example.data.WordsDao
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)

class WordsListViewModelTest {
    @get:Rule(order = 1)
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    private lateinit var database: AppDatabase
    private lateinit var dao: WordsDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.wordDao()
    }

    @After
    fun tearDown() {
        database.close()
    }
    @Test
    fun testViewModelLoadsData() = runTest {
        val sync = SyncDictionaryDefault(database)
        sync.insertUsingSqlite(createWordsSequence(size = 1000))
        val viewModel = WordsListViewModel(dao)
        val items = viewModel.items
        val itemsSnapshot = items.asSnapshot {
            // Scroll to the 50th item in the list. This will also suspend till
            // the prefetch requirement is met if there's one.
            // It also suspends until all loading is complete.
            scrollTo(index = 300)
        }
        assertThat(itemsSnapshot).hasSize(500)

        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 500) })).hasSize(700)
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 700) })).hasSize(900)
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 850) })).hasSize(1000)
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 900) })).hasSize(1000)
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 999) })).hasSize(1000)

    }
    @Test
    fun testViewModelLoadsHugeData() = runTest {
//        val sync = SyncDictionaryDefault(database, chunkSize = 1000)
        val sync = SyncDictionaryDefault(database)
//        val sync = SyncDictionaryDefault(database, chunkSize = 10_000)
        sync.insertUsingSqlite(createWordsSequence(size = 100_000))
        val viewModel = WordsListViewModel(dao)
        val items = viewModel.items
        val itemsSnapshot = items.asSnapshot {
            // Scroll to the 50th item in the list. This will also suspend till
            // the prefetch requirement is met if there's one.
            // It also suspends until all loading is complete.
            scrollTo(index = 300)
        }
        assertThat(itemsSnapshot).hasSize(500)

        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 500) })).hasSize(700)
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 700) })).hasSize(900)
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 850) })).hasSize(1000)
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 5_000) })).hasSize(5200)
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 5_200) })).hasSize(5400)

    }

    private fun createWordsSequence(size: Int) = (1..size).map { "$it" }.asSequence()

}