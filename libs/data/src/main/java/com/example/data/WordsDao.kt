package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordsDao {
    @Query("select * from words")
    fun observeWords(): Flow<List<WordEntity>>
    @Query("select * from words")
    suspend fun getAllWords(): List<WordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(words: List<WordEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: WordEntity)
}