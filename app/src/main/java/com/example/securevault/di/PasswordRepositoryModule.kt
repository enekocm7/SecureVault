package com.example.securevault.di

import com.example.securevault.data.json.crypto.FileEncryptor
import com.example.securevault.data.json.storage.PasswordStorage
import com.example.securevault.data.repository.PasswordRepositoryImpl
import com.example.securevault.domain.repository.PasswordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PasswordRepositoryModule {
    @Provides
    @Singleton
    fun providePasswordRepository(storage: PasswordStorage,encryptor: FileEncryptor): PasswordRepository{
        return PasswordRepositoryImpl(storage,encryptor)
    }
}