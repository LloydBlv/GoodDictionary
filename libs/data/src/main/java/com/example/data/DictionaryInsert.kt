package com.example.data

interface DictionaryInsert {
    fun insertUsingSqlite(words: Sequence<String>)
    fun insertUsingDao(words: Sequence<String>)
}