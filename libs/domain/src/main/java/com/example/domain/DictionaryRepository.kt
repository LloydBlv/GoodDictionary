package com.example.domain

import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {
    suspend fun deleteWord(wordId: Long)
    fun getWord(wordId: Long): Flow<DictionaryWord>
}