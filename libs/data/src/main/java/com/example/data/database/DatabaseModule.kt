package com.example.data.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {

  companion object {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
      return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "good_dictionary_db",
      ).build()
    }

    @Provides
    @Singleton
    fun provideWordsDao(appDatabase: AppDatabase): WordsDao {
      return appDatabase.wordDao()
    }
  }
}
