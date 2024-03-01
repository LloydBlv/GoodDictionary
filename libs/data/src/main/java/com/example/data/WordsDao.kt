package com.example.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.ColumnInfo
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
    suspend fun getCount(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(words: List<WordEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: WordEntity)

    @Query("SELECT substr(word, 1, 1) AS firstChar, GROUP_CONCAT(word, ', ') AS words FROM words GROUP BY firstChar")
    fun getWordsGroupedByFirstCharacter(): LiveData<List<WordsGroupedByFirstCharacter>>

    // Define a data class to hold the query result
    data class WordsGroupedByFirstCharacter(
        @ColumnInfo(name = "firstChar") val firstCharacter: String,
        @ColumnInfo(name = "words") val words: String // This will be a concatenated string of words
    )

    @Query("SELECT * FROM words ORDER BY rowid ASC")
    fun allWordsPaged(): PagingSource<Int, WordEntity>

    @Query("SELECT * FROM words WHERE word LIKE :query ORDER BY rowid ASC")
    fun filtered1(query: String): PagingSource<Int, WordEntity>

    @Query(
        "SELECT snippet(wordsFts) FROM words JOIN wordsFts " +
                "ON words.rowid == wordsFts.rowid WHERE wordsFts.word " +
                "MATCH :search ORDER BY sequence"
    )
    fun filtered(search: String): PagingSource<Int, String>

}