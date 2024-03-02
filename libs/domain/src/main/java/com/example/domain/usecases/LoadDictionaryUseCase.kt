package com.example.domain.usecases

import com.example.domain.DictionaryLoader
import com.example.domain.DictionaryState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadDictionaryUseCase @Inject constructor(private val dictionaryRepository: DictionaryLoader) {
    operator fun invoke(): Flow<DictionaryState> {
        return dictionaryRepository.load()
    }
}