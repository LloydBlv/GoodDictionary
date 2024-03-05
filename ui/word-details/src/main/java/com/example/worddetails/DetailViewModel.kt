package com.example.worddetails

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.DictionaryWord
import com.example.domain.usecases.DeleteWordUseCase
import com.example.domain.usecases.GetWordUseCase
import com.example.domain.usecases.GetWordsCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
  private val deleteWordUseCase: dagger.Lazy<DeleteWordUseCase>,
  getWordsCount: GetWordsCountUseCase,
  getWordUseCase: GetWordUseCase,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

  private val wordId: Long
    get() = requireNotNull(savedStateHandle.get<Long>("wordId"))

  val state = combine(
    getWordUseCase.invoke(wordId),
    getWordsCount.invoke(),
    ::Pair,
  )
    .map { (word, count) ->
      DetailUiState(word = word, count = count, isLoading = false)
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
      initialValue = DetailUiState(isLoading = true),
    )

  fun deleteWord() {
    viewModelScope.launch {
      deleteWordUseCase.get().invoke(wordId)
    }
  }

  @Immutable
  data class DetailUiState(
    val isLoading: Boolean,
    val word: DictionaryWord? = null,
    val count: Long = 0,
  )
}
