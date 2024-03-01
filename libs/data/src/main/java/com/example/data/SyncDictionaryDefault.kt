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
            var sequence = 0L
            if (hasValidChunk()) {

                words.chunked(chunkSize!!)
                    .forEach {

//                        val values = ContentValues()
//
//                        it.forEach {
//                            values.put("word", it)
//                            values.put("sequence", sequence)
//                            insertUsingSqlite(db, values)
//                            values.clear()
//                        }
//                        sequence += 1
                        insertUsingBindings(it, sequence++, db)
                    }
            } else {
                insertUsingBindings(words.toList(), sequence++, db)
//                val values = ContentValues()
//                words.forEach {
//                    values.put("word", it)
//                    values.put("sequence", sequence)
//                    insertUsingSqlite(db, values)
//                    values.clear()
//                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    var rowId = 0L
    private fun insertUsingBindings(items: List<String>,
                                    sequence: Long,
                                    db: SupportSQLiteDatabase) {
        val sql = "INSERT INTO words VALUES(?, ?, ?)"
        val statement = db.compileStatement(sql)
        db.beginTransaction()
        try {
            for (item in items) {
                statement.clearBindings()
                statement.bindLong(1, rowId)
                statement.bindLong(2, sequence)
                statement.bindString(3, item)
                statement.execute()
                rowId++
            }
            db.setTransactionSuccessful()
        } finally {
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
            var sequence = 0L
            words.chunked(chunkSize!!)
                .onEach { insertUsingDao(it, sequence++) }
            return
        }
        words.onEach { database.wordDao().insert(WordEntity(word = it, sequence = 1)) }

    }

    private fun insertUsingDao(it: List<String>, sequence: Long) {
        database.wordDao().insert(it.map { WordEntity(word = it, sequence = sequence) })
    }

    private fun hasValidChunk(): Boolean = chunkSize != null && chunkSize > 0
}