package com.example.words_list

import assertk.assertThat
import assertk.assertions.isTrue
import assertk.assertions.prop
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class WordsListPresenterTest {

    @Test
    fun testPresenterInitialState() = runTest {
        val presenter = WordsListPresenter()
        presenter.test {
            assertThat(awaitItem()).prop(WordsListUiState::isLoading).isTrue()
        }
    }
}