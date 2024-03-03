package com.example.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionary_sync.DictionarySyncStateWatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    stateWatcher: DictionarySyncStateWatcher
) : ViewModel() {
    fun onRetryClicked() {

    }

    val state = stateWatcher.watch()
        .map {
            when (it) {
                DictionarySyncStateWatcher.State.Loaded -> UiState.Loaded
                DictionarySyncStateWatcher.State.Loading -> UiState.Loading
                is DictionarySyncStateWatcher.State.Progress -> UiState.Progress(it.percent)
                DictionarySyncStateWatcher.State.Blocked -> UiState.Blocked
                DictionarySyncStateWatcher.State.Cancelled -> UiState.Cancelled
                is DictionarySyncStateWatcher.State.Failure -> UiState.Failure(it.message)
                DictionarySyncStateWatcher.State.Unknown -> UiState.Unknown
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )


    sealed interface UiState {
        data object Loading : UiState
        data object Loaded : UiState
        data object Blocked : UiState
        data object Cancelled : UiState
        data object Unknown : UiState
        data class Failure(val message: String?) : UiState
        data class Progress(val percent: Int) : UiState
    }
}