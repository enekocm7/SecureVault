package com.enekocm.securevault.di

import com.enekocm.securevault.data.json.crypto.FileEncryptor
import com.enekocm.securevault.data.json.storage.PasswordStorage
import com.enekocm.securevault.data.repository.PasswordRepositoryImpl
import com.enekocm.securevault.domain.repository.PasswordRepository
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
    fun providePasswordRepository(
        storage: PasswordStorage,
        encryptor: FileEncryptor
    ): PasswordRepository {
        return PasswordRepositoryImpl(storage, encryptor)
    }
}
