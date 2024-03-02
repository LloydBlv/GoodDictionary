package com.example.domain.usecases

import androidx.paging.PagingData
import com.example.domain.DictionaryRepository
import com.example.domain.DictionaryWord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilteredWordsUseCase @Inject constructor(
    private val repository: DictionaryRepository
) {
    operator fun invoke(query: String): Flow<PagingData<DictionaryWord>> {
        return repository.getFilteredWords(query)
    }
}