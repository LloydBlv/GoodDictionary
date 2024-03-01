package com.example.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadDictionaryUseCase @Inject constructor(private val dictionaryRepository: DictionaryLoader) {
    operator fun invoke(): Flow<DictionaryState> {
        return dictionaryRepository.load()
    }
}