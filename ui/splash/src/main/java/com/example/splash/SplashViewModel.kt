package com.example.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionary_sync.DictionarySyncStateWatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    stateWatcher: DictionarySyncStateWatcher
) : ViewModel() {
    val state: StateFlow<DictionarySyncStateWatcher.State> = stateWatcher.watch()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DictionarySyncStateWatcher.State.Loading
        )
}