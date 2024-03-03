package com.example.domain.usecases

import com.example.domain.DictionaryWord
import com.example.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWordUseCase @Inject constructor(
    private val repository: DictionaryRepository
) {
    operator fun invoke(wordId: Long): Flow<DictionaryWord> {
        return repository.getWord(wordId)
    }
}