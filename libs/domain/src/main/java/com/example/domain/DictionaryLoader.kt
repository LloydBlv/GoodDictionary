package com.example.domain

import kotlinx.coroutines.flow.Flow


sealed interface DictionaryState {
    data object Loading: DictionaryState
    data object ParsingItems: DictionaryState
    data class Loaded(val wordsCount: Long): DictionaryState
}
interface DictionaryLoader {
    fun load(): Flow<DictionaryState>
}