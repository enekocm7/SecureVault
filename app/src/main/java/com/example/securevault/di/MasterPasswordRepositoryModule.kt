package com.example.securevault.di

import com.example.securevault.data.repository.MasterPasswordRepositoryImpl
import com.example.securevault.data.storage.AppKeyStorage
import com.example.securevault.domain.repository.MasterPasswordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MasterPasswordRepositoryModule {

    @Provides
    @Singleton
    fun provideMasterPasswordRepository(storage: AppKeyStorage): MasterPasswordRepository {
        return MasterPasswordRepositoryImpl(storage)
    }
}