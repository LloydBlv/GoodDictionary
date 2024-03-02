package com.example.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "words", indices = [Index(value = ["word"], unique = true)])
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val rowid: Long = 0,
    @ColumnInfo(name = "sequence") val sequence: Long = 0,
    @ColumnInfo(name = "word") val word: String
)

@Fts4(contentEntity = WordEntity::class)
@Entity(tableName = "wordsFts")
data class WordsFtsEntity(val word: String)
