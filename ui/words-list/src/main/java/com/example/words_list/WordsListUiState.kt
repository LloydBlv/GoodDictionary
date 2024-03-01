package com.example.words_list

import com.slack.circuit.runtime.CircuitUiState

data class WordsListUiState(
    val isLoading: Boolean
) : CircuitUiState