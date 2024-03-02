package com.example.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {
    suspend fun deleteWord(wordId: Long)
    fun getCount(): Flow<Long>
    fun getWord(wordId: Long): Flow<DictionaryWord>
    fun getFilteredWords(query: String): Flow<PagingData<DictionaryWord>>
}