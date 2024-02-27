package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val rowid: Int = 0,
    @ColumnInfo(name = "word") val word: String
)