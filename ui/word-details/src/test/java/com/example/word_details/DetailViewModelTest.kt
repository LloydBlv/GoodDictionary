package com.example.word_details

import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import assertk.assertions.prop
import com.example.data.database.AppDatabase
import com.example.data.database.DatabaseModule
import com.example.data.database.WordsDao
import com.example.domain.DictionaryWord
import com.example.domain.repository.DictionaryRepository
import com.example.domain.usecases.DeleteWordUseCase
import com.example.domain.usecases.GetWordUseCase
import com.example.domain.usecases.GetWordsCountUseCase
import com.example.domain.usecases.SyncDictionaryRecordsUseCase
import com.example.testing.MainDispatcherRule
import com.example.testing.TimberRule
import com.example.worddetails.DetailViewModel
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
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
class DetailViewModelTest {

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
  fun `initially state is set correctly`() = runTest {
    insertRecordsUseCase.invoke(generateTestData(1000))
    val detailViewModel = DetailViewModel(
      deleteWordUseCase = { DeleteWordUseCase(repository) },
      getWordsCount = GetWordsCountUseCase(repository),
      getWordUseCase = GetWordUseCase(repository),
      savedStateHandle = SavedStateHandle(mapOf("wordId" to 1L))
    )
    detailViewModel.state.test {
      assertThat(awaitItem()).transform { it.isLoading }.isTrue()
      assertThat(awaitItem()).all {
        prop(DetailViewModel.DetailUiState::isLoading).isFalse()
        prop(DetailViewModel.DetailUiState::count).isEqualTo(1000)
        prop(DetailViewModel.DetailUiState::word).isEqualTo(DictionaryWord("1", 1))
      }
      ensureAllEventsConsumed()
    }
  }
  @Test
  fun `when delete requested, word is deleted correctly`() = runTest {
    insertRecordsUseCase.invoke(generateTestData(1000))
    val detailViewModel = DetailViewModel(
      deleteWordUseCase = { DeleteWordUseCase(repository) },
      getWordsCount = GetWordsCountUseCase(repository),
      getWordUseCase = GetWordUseCase(repository),
      savedStateHandle = SavedStateHandle(mapOf("wordId" to 1L))
    )
    detailViewModel.deleteWord(1)
    repository.getCount().test {
      assertThat(awaitItem()).isEqualTo(999)
    }
    repository.getWord(1).test {
      ensureAllEventsConsumed()
    }
  }

  private fun generateTestData(size: Int): List<String> {
    return (1..size).map { "$it" }
  }

}
