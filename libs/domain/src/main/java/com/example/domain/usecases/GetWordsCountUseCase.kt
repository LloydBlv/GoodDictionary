package com.example.domain.usecases

import com.example.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWordsCountUseCase @Inject constructor(private val repository: DictionaryRepository) {
    operator fun invoke(): Flow<Long> {
        return repository.getCount()
    }
}