package com.example.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.data.database.WordsDao
import com.example.domain.DictionaryWord
import com.example.domain.repository.DictionaryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class RealDictionaryRepository @Inject constructor(
  private val dao: WordsDao,
) : DictionaryRepository {
  override suspend fun deleteWord(wordId: Long) {
    dao.deleteById(wordId)
  }

  override fun getCount(): Flow<Long> {
    return dao.getCount()
  }

  override fun getWord(wordId: Long): Flow<DictionaryWord> {
    return dao.getWordById(wordId)
      .filterNotNull()
      .map { entity ->
        DictionaryWord(
          word = entity.word,
          id = entity.rowid,
        )
      }
  }

  override fun getFilteredWords(query: String): Flow<PagingData<DictionaryWord>> {
    return Pager(
      config = PagingConfig(pageSize = 100),
      initialKey = null,
      pagingSourceFactory = { dao.filtered1("%$query%") },
    ).flow.map {
      it.map { entity -> DictionaryWord(word = entity.word, id = entity.rowid) }
    }
  }
}
