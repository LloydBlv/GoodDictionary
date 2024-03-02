package com.example.domain.usecases

import com.example.domain.DictionaryLoader
import javax.inject.Inject

class SyncDictionaryRecordsUseCase @Inject constructor(
    private val dictionaryLoader: DictionaryLoader,
){
    suspend operator fun invoke(records: List<String>) {
        dictionaryLoader.sync(records)
    }

}