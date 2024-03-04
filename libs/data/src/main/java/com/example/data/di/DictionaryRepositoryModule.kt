package com.example.data.di

import com.example.data.DictionaryInsert
import com.example.data.DictionaryInsertDefault
import com.example.data.repository.DictionaryRepositoryDefault
import com.example.data.repository.RealDictionaryRepository
import com.example.domain.DictionaryLoader
import com.example.domain.repository.DictionaryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DictionaryRepositoryModule {
  @Binds
  fun provideDictionaryRepository(default: DictionaryRepositoryDefault): DictionaryLoader

  @Binds
  fun provideSyncDictionary(default: DictionaryInsertDefault): DictionaryInsert

  @Binds
  fun provideDictionaryRepoReal(real: RealDictionaryRepository): DictionaryRepository
}
