package com.example.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.DictionarySyncStateWatcher
import com.example.domain.DictionarySyncStateWatcher.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SplashViewModel @Inject constructor(
  private val stateWatcher: DictionarySyncStateWatcher,
) : ViewModel() {
  fun onRetryClicked() {
    stateWatcher.retry()
  }

  val state = stateWatcher.watch()
    .map {
      when (it) {
        State.Loaded -> UiState.Loaded
        State.Loading -> UiState.Loading
        is State.Progress -> UiState.Progress(it.percent)
        State.Blocked -> UiState.Blocked
        State.Cancelled -> UiState.Cancelled
        is State.Failure -> UiState.Failure(it.message)
        State.Unknown -> UiState.Unknown
      }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = UiState.Loading,
    )

  sealed interface UiState {
    fun isLoading() = this is Loading || this is Progress
    data object Loading : UiState
    data object Loaded : UiState
    data object Blocked : UiState
    data object Cancelled : UiState
    data object Unknown : UiState
    data class Failure(val message: String?) : UiState
    data class Progress(val percent: Int) : UiState
  }
}
