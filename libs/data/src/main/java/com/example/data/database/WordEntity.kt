package com.example.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "words", indices = [Index(value = ["word"], unique = true)])
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val rowid: Long = 0,
    @ColumnInfo(name = "word") val word: String
)
