package com.example.dictionary_sync

import kotlinx.coroutines.flow.Flow


interface DictionarySyncStateWatcher {
    sealed interface State {
        data object Loading: State
        data object Loaded: State
        data object Cancelled: State
        data object Blocked: State
        data object Unknown: State
        data class Progress(val percent: Int): State
        data class Failure(val message: String?): State
    }
    fun watch(): Flow<State>
    fun retry()
}