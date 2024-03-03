package com.example.domain.usecases

import com.example.domain.repository.DictionaryRepository
import javax.inject.Inject

class DeleteWordUseCase @Inject constructor(private val repository: DictionaryRepository) {
    suspend operator fun invoke(wordId: Long) {
        return repository.deleteWord(wordId)
    }
}