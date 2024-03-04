package com.example.data

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import assertk.all
import assertk.assertThat
import assertk.assertions.first
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import assertk.assertions.prop
import com.example.data.TestData.generateTestWords
import com.example.data.database.AppDatabase
import com.example.data.database.WordEntity
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
class WordsDatabaseTest {

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
  fun `initially database is empty`() = runTest {
    assertThat(dao.getAllWords().size).isEqualTo(0)
  }

  @Test
  fun `after insertion, items are stored in the db`() = runTest {
    dao.insert(generateTestWords())
    assertThat(dao.getAllWords()).all {
      hasSize(100)
      first().prop(WordEntity::word).isEqualTo("1")
      transform { it.last() }.prop(WordEntity::word).isEqualTo("100")
    }
  }

  @Test
  fun `after reinsertion, items are stored in the db`() = runTest {
    dao.insert(generateTestWords())
    assertThat(dao.getAllWords()).all {
      hasSize(100)
      first().prop(WordEntity::word).isEqualTo("1")
      transform { it.last() }.prop(WordEntity::word).isEqualTo("100")
    }
    dao.insert(generateTestWords())
    assertThat(dao.getAllWords()).all {
      hasSize(100)
      first().prop(WordEntity::word).isEqualTo("1")
      transform { it.last() }.prop(WordEntity::word).isEqualTo("100")
    }
  }

  @Test
  fun `after insertion, items are stored and observed correctly in db`() = runTest {
    dao.observeWords().test {
      assertThat(awaitItem()).hasSize(0)
      dao.insert(generateTestWords())
      assertThat(awaitItem()).all {
        hasSize(100)
        first().prop(WordEntity::word).isEqualTo("1")
        transform { it.last() }.prop(WordEntity::word).isEqualTo("100")
      }
    }
  }

  @Test
  fun `after reinsertion, items are stored and observed correctly in db`() = runTest {
    dao.observeWords().test {
      assertThat(awaitItem()).hasSize(0)
      dao.insert(generateTestWords())
      assertThat(awaitItem()).all {
        hasSize(100)
        first().prop(WordEntity::word).isEqualTo("1")
        transform { it.last() }.prop(WordEntity::word).isEqualTo("100")
      }
      dao.insert(generateTestWords())
      assertThat(awaitItem()).all {
        hasSize(100)
        first().prop(WordEntity::word).isEqualTo("1")
        transform { it.last() }.prop(WordEntity::word).isEqualTo("100")
      }
    }
  }
}
