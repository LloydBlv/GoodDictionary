package com.example.word_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.DictionaryWord
import com.example.domain.usecases.GetWordUseCase
import com.example.domain.usecases.DeleteWordUseCase
import com.example.domain.usecases.GetWordsCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


@HiltViewModel
class DetailViewModel @Inject constructor(
    private val deleteWordUseCase: dagger.Lazy<DeleteWordUseCase>,
    private val getWordsCount: GetWordsCountUseCase,
    getWordUseCase: GetWordUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val wordId: Long
        get() = requireNotNull(savedStateHandle.get<Long>("wordId"))

    val state = combine(
        getWordUseCase
            .invoke(wordId), getWordsCount.invoke(), ::Pair
    )
        .map { (word, count) ->
            DetailUiState(word = word, count = count, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = DetailUiState(isLoading = true)
        )

    fun deleteWord(wordId: Long) {
        viewModelScope.launch {
            deleteWordUseCase.get().invoke(wordId)
        }
    }

    data class DetailUiState(
        val isLoading: Boolean,
        val word: DictionaryWord? = null,
        val count: Long = 0,
    )
}