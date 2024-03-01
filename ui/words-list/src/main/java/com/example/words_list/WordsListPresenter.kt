package com.example.words_list

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.presenter.Presenter


class WordsListPresenter : Presenter<WordsListUiState> {
    @Composable
    override fun present(): WordsListUiState {
        return WordsListUiState(isLoading = true)
    }
}
