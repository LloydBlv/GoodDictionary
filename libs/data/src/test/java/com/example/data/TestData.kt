package com.example.data

import com.example.data.database.WordEntity

object TestData {
  fun generateTestWords(dataSize: Int = 100): List<WordEntity> {
    return (1..dataSize).map {
      WordEntity(
        rowid = it.toLong(),
        word = "$it",
      )
    }
  }
}
