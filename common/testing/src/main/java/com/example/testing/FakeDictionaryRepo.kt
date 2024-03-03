package com.example.testing

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.testing.asPagingSourceFactory
import com.example.domain.DictionaryWord
import com.example.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow


class FakeDictionaryRepo(private val words: Sequence<String>) : DictionaryRepository {
    override suspend fun deleteWord(wordId: Long) {
        TODO("Not yet implemented")
    }

    override fun getCount(): Flow<Long> {
        TODO("Not yet implemented")
    }

    override fun getWord(wordId: Long): Flow<DictionaryWord> {
        TODO("Not yet implemented")
    }

    override fun getFilteredWords(query: String): Flow<PagingData<DictionaryWord>> {
        var id = 0L
        val words: List<DictionaryWord> = words
            .toList()
            .map { DictionaryWord(word = it, id = id++) }
        return Pager(
            config = PagingConfig(pageSize = 100),
            initialKey = null,
            pagingSourceFactory = {
                words.asPagingSourceFactory().invoke()
            }
        ).flow
    }
}