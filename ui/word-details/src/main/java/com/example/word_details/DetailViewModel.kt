package com.example.word_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.DeleteWordUseCase
import com.example.domain.DictionaryWord
import com.example.domain.GetWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


@HiltViewModel
class DetailViewModel @Inject constructor(
    private val deleteWordUseCase: dagger.Lazy<DeleteWordUseCase>,
    getWordUseCase: GetWordUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val wordId: Long
        get() = requireNotNull(savedStateHandle.get<Long>("wordId"))

    val state = getWordUseCase
        .invoke(wordId)
        .map(DetailUiState::Loaded)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = DetailUiState.Loading
        )

    fun deleteWord(wordId: Long) {
        viewModelScope.launch {
            deleteWordUseCase.get().invoke(wordId)
        }
    }

    sealed interface DetailUiState {
        data object Loading : DetailUiState
        data class Loaded(val word: DictionaryWord) : DetailUiState
    }
}