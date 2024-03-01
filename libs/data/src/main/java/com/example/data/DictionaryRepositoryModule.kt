package com.example.data

import com.example.domain.DictionaryLoader
import com.example.domain.DictionaryRepository
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
    fun provideSyncDictionary(default: SyncDictionaryDefault): SyncDictionary

    @Binds
    fun provideDictionaryRepoReal(real: RealDictionaryRepository): DictionaryRepository
}