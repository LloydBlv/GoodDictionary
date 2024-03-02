package com.example.data.repository

import com.example.data.DictionaryInsert
import com.example.domain.DictionaryLoader
import javax.inject.Inject


class DictionaryRepositoryDefault @Inject constructor(
    private val syncDictionary: DictionaryInsert,
) : DictionaryLoader {


    override suspend fun sync(records: List<String>) {
        //TODO fix me, pass list instead
        syncDictionary.insertUsingSqlite(records.asSequence())
    }

}