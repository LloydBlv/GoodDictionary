package com.example.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase


class SyncDictionaryDefault(
    private val database: AppDatabase,
    private val chunkSize: Int? = null
) : SyncDictionary {
    override fun insertUsingSqlite(words: Sequence<String>) {
        val db = database.openHelper.writableDatabase
        try {
            db.beginTransaction()
            if (hasValidChunk()) {
                words.chunked(chunkSize!!)
                    .forEach {
                        val values = ContentValues()
                        it.forEach { values.put("word", it) }
                        insertUsingSqlite(db, values)
                    }
            } else {
                val values = ContentValues()
                words.forEach { values.put("word", it) }
                insertUsingSqlite(db, values)
            }
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
    }

    private fun insertUsingSqlite(
        db: SupportSQLiteDatabase,
        values: ContentValues
    ) {
        db.insert("words", SQLiteDatabase.CONFLICT_REPLACE, values)
    }

    override fun insertUsingDao(words: Sequence<String>) {
        if (hasValidChunk()) {
            words.chunked(chunkSize!!)
                .onEach {insertUsingDao(it) }
            return
        }
        words.onEach { database.wordDao().insert(WordEntity(word = it)) }

    }

    private fun insertUsingDao(it: List<String>) {
        database.wordDao().insert(it.map { WordEntity(word = it) })
    }

    private fun hasValidChunk(): Boolean = chunkSize != null && chunkSize > 0
}