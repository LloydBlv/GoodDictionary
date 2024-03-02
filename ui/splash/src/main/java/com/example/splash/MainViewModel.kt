package com.example.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.DictionaryState
import com.example.domain.usecases.LoadDictionaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    loadDictionary: LoadDictionaryUseCase
) : ViewModel() {
    val state: StateFlow<DictionaryState> = loadDictionary.invoke()
        .debounce(300)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DictionaryState.Loading)
}