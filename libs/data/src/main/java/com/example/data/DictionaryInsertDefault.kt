package com.example.data

import android.content.ContentValues
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.database.AppDatabase
import com.example.data.database.WordEntity
import javax.inject.Inject
import timber.log.Timber

class DictionaryInsertDefault @Inject constructor(
  private val database: AppDatabase,
) : DictionaryInsert {

  var chunkSize: Int? = null

  override fun insertUsingSqlite(words: Sequence<String>) {
    val db = database.openHelper.writableDatabase
    try {
      db.beginTransaction()
      if (hasValidChunk()) {
        words
          .chunked(chunkSize!!)
          .forEach { insertUsingBindings(it, db) }
      } else {
        insertUsingBindings(words.toList(), db)
      }
      db.setTransactionSuccessful()
    } finally {
      db.endTransaction()
    }
  }

  private fun insertUsingBindings(
    items: List<String>,
    db: SupportSQLiteDatabase,
  ) {
    val sql = "INSERT INTO words VALUES(?, ?)"
    val statement = db.compileStatement(sql)
    db.beginTransaction()
    try {
      for (item in items) {
        statement.clearBindings()
        statement.bindString(2, item)
        statement.executeInsert()
      }
      db.setTransactionSuccessful()
    } catch (sqlConstraint: SQLiteConstraintException) {
      Timber.e(sqlConstraint, "insertion failed")
    } finally {
      db.endTransaction()
    }
  }

  private fun insertUsingSqlite(
    db: SupportSQLiteDatabase,
    values: ContentValues,
  ) {
    db.insert("words", SQLiteDatabase.CONFLICT_REPLACE, values)
  }

  override fun insertUsingDao(words: Sequence<String>) {
    if (hasValidChunk()) {
      words.chunked(chunkSize!!)
        .onEach { insertUsingDao(it) }
      return
    }
    words.onEach { database.wordDao().insert(WordEntity(word = it)) }
  }

  private fun insertUsingDao(it: List<String>) {
    database.wordDao().insert(it.map { WordEntity(word = it) })
  }

  private fun hasValidChunk(): Boolean = chunkSize?.let { it > 0 } == true
}
