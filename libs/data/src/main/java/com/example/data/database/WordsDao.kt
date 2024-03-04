package com.example.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordsDao {
  @Query("select * from words")
  fun observeWords(): Flow<List<WordEntity>>

  @Query("DELETE FROM words WHERE rowid = :id")
  suspend fun deleteById(id: Long)

  @Query("SELECT * FROM words WHERE rowid = :id")
  fun getWordById(id: Long): Flow<WordEntity?>

  @Query("select * from words")
  suspend fun getAllWords(): List<WordEntity>

  @Query("SELECT COUNT(*) FROM words")
  fun getCount(): Flow<Long>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(words: List<WordEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(words: List<WordEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(word: WordEntity)


  @Query("SELECT * FROM words ORDER BY rowid ASC")
  fun allWordsPaged(): PagingSource<Int, WordEntity>

  @Query("SELECT * FROM words WHERE word LIKE :query ORDER BY rowid ASC")
  fun filtered(query: String): PagingSource<Int, WordEntity>
}
