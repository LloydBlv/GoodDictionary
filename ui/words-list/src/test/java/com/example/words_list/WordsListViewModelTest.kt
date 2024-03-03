package com.example.words_list

import androidx.paging.testing.asSnapshot
import assertk.Assert
import assertk.assertThat
import assertk.assertions.isTrue
import assertk.assertions.size
import com.example.dictionary_sync.DictionarySyncStateWatcherDefault
import com.example.domain.repository.DictionaryRepository
import com.example.domain.usecases.GetFilteredWordsUseCase
import com.example.testing.DataSyncStatusFake
import com.example.testing.FakeDictionaryRepo
import com.example.testing.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.time.Duration.Companion.seconds


@RunWith(RobolectricTestRunner::class)

class WordsListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun testViewModelLoadsData() = runTest(timeout = 2.seconds) {
        val fakeRepository: DictionaryRepository = FakeDictionaryRepo(createWordsSequence(size = 1_000))
        val viewModel = WordsListViewModel(
            getFilteredWordUseCase = GetFilteredWordsUseCase(fakeRepository),
            stateWatcher = DictionarySyncStateWatcherDefault(DataSyncStatusFake())
        )
        val items = viewModel.pagingDataFlow
        val itemsSnapshot = items.asSnapshot {
            // Scroll to the 50th item in the list. This will also suspend till
            // the prefetch requirement is met if there's one.
            // It also suspends until all loading is complete.
            scrollTo(index = 300)
        }
        assertThat(itemsSnapshot.size >= 300).isTrue()

        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 500) }).size >= 500).isTrue()
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 700) }).size >= 700).isTrue()
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 850) }).size >= 850).isTrue()
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 900) }).size >= 900).isTrue()
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 1000) })).transform { it.size >= 1000 }.isTrue()

    }

    @Test
    fun testViewModelLoadsHugeData() = runTest {
        val fakeRepository: DictionaryRepository = FakeDictionaryRepo(createWordsSequence(size = 1000_000))
        val viewModel = WordsListViewModel(
            getFilteredWordUseCase = GetFilteredWordsUseCase(fakeRepository),
            stateWatcher = DictionarySyncStateWatcherDefault(DataSyncStatusFake())
        )
        val items = viewModel.pagingDataFlow
        val itemsSnapshot = items.asSnapshot {
            scrollTo(index = 300)
        }
        assertThat(itemsSnapshot).isAtLeast(422)

        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 500) })).isAtLeast(500)
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 700) })).isAtLeast(700)
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 850) })).isAtLeast(850)
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 10_000) })).isAtLeast(10_000)
        assertThat(items.asSnapshot(loadOperations = { scrollTo(index = 12_000) })).isAtLeast(12_000)

    }

    private fun createWordsSequence(size: Int) = (1..size).map { "$it" }.asSequence()

    fun Assert<List<*>>.isAtLeast(size: Int) {
        size().transform { it >= size }.isTrue()
    }

}