package com.example.data

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.example.data.database.AppDatabase
import com.example.data.database.WordsDao
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DictionaryInsertDefaultTest {
  private lateinit var database: AppDatabase
  private lateinit var dao: WordsDao

  @JvmField
  @Rule
  val countingTaskExecutorRule = CountingTaskExecutorRule()

  @Before
  fun setup() {
    database = Room.inMemoryDatabaseBuilder(
      InstrumentationRegistry.getInstrumentation().context,
      AppDatabase::class.java,
    ).allowMainThreadQueries().build()
    dao = database.wordDao()
  }

  @After
  fun tearDown() {
    database.close()
    // At the end of all tests, query executor should be idle (transaction thread released).
    countingTaskExecutorRule.drainTasks(500, TimeUnit.MILLISECONDS)
    assertThat(countingTaskExecutorRule.isIdle).isTrue()
  }

  @Test
  fun testWordsAreInsertedCorrectly() = runTest {
    val sync = DictionaryInsertDefault(database).apply {
      chunkSize = 1000
    }
    val wordsSequence = createWordsSequence()
    sync.insertUsingSqlite(wordsSequence)
    assertThat(dao.getAllWords().size).isEqualTo(370150)
  }

  private fun createWordsSequence() = (1..370150).map { "$it" }.asSequence()
}
