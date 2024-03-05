package com.example.worddetails

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.data.database.AppDatabase
import com.example.data.database.DatabaseModule
import com.example.data.database.WordsDao
import com.example.domain.repository.DictionaryRepository
import com.example.domain.usecases.DeleteWordUseCase
import com.example.domain.usecases.GetWordUseCase
import com.example.domain.usecases.GetWordsCountUseCase
import com.example.domain.usecases.SyncDictionaryRecordsUseCase
import com.example.testing.MainDispatcherRule
import com.example.testing.TimberRule
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import io.mockk.mockk
import io.mockk.verify
import javax.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
@Config(application = HiltTestApplication::class)
class WordDetailScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @get:Rule
  val timberRule = TimberRule()

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  @get:Rule
  var hiltRule = HiltAndroidRule(this)

  @Inject
  lateinit var repository: DictionaryRepository

  @Inject
  lateinit var insertRecordsUseCase: SyncDictionaryRecordsUseCase

  @JvmField
  @BindValue
  val database: AppDatabase = Room.inMemoryDatabaseBuilder(
    InstrumentationRegistry.getInstrumentation().context,
    AppDatabase::class.java,
  ).allowMainThreadQueries().build()

  @JvmField
  @BindValue
  val dao: WordsDao = database.wordDao()

  @Before
  fun setup() {
    hiltRule.inject()
  }

  @Test
  fun `when delete button clicked word is removed from database`() = runTest {
    val detailViewModel = initializeViewModel()
    composeTestRule.setContent {
      val state by detailViewModel.state.collectAsState()
      WordDetailContent(
        state = state,
        onBackPressed = {},
        onWordDeleted = {},
        onDeleteWordClicked = detailViewModel::deleteWord
      )
    }
    composeTestRule.onNodeWithText("Yes, Remove!").performClick()
    repository.getCount().test {
      assertThat(awaitItem()).isEqualTo(999)
    }
    repository.getWord(1).test {
      ensureAllEventsConsumed()
    }
  }
  @Test
  fun `when delete button clicked onWordDeleted callback is invoked`() = runTest {
    val onWordDeletedCallback: () -> Unit = mockk(relaxed = true)
    val detailViewModel = initializeViewModel()
    composeTestRule.setContent {
      val state by detailViewModel.state.collectAsState()
      WordDetailContent(
        state = state,
        onBackPressed = {},
        onWordDeleted = onWordDeletedCallback,
        onDeleteWordClicked = detailViewModel::deleteWord
      )
    }
    composeTestRule.onNodeWithText("Yes, Remove!").performClick()
    verify { onWordDeletedCallback.invoke() }
  }
  @Test
  fun `when state loaded, word is displayed on the screen`() = runTest {
    val detailViewModel = initializeViewModel()
    composeTestRule.setContent {
      val state by detailViewModel.state.collectAsState()
      WordDetailContent(
        state = state,
        onBackPressed = {},
        onWordDeleted = {},
        onDeleteWordClicked = detailViewModel::deleteWord
      )
    }
    composeTestRule.onNodeWithText("1").isDisplayed()
  }
  @Test
  fun `when state loaded, delete snackbar is displayed on the screen`() = runTest {
    val detailViewModel = initializeViewModel()
    composeTestRule.setContent {
      val state by detailViewModel.state.collectAsState()
      WordDetailContent(
        state = state,
        onBackPressed = {},
        onWordDeleted = {},
        onDeleteWordClicked = detailViewModel::deleteWord
      )
    }
    val removeThisWord =
      InstrumentationRegistry.getInstrumentation().context.getString(R.string.remove_this_word)
    composeTestRule.run {
      onNodeWithText(removeThisWord).isDisplayed()
      onNodeWithText("Yes, Remove!").isDisplayed()
    }
  }

  private suspend fun initializeViewModel(): DetailViewModel {
    insertRecordsUseCase.invoke(generateTestData(1000))
    val detailViewModel = DetailViewModel(
      deleteWordUseCase = { DeleteWordUseCase(repository) },
      getWordsCount = GetWordsCountUseCase(repository),
      getWordUseCase = GetWordUseCase(repository),
      savedStateHandle = SavedStateHandle(mapOf("wordId" to 1L))
      )
    return detailViewModel
  }

  private fun generateTestData(size: Int): List<String> {
    return (1..size).map { "$it" }
  }

}

