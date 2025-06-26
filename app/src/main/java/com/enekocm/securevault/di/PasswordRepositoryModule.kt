package com.enekocm.securevault.di

import com.enekocm.securevault.data.json.crypto.FileEncryptor
import com.enekocm.securevault.data.json.storage.PasswordStorage
import com.enekocm.securevault.data.repository.FirebasePasswordRepository
import com.enekocm.securevault.data.repository.LocalPasswords
import com.enekocm.securevault.data.repository.PasswordRepositoryImpl
import com.enekocm.securevault.data.repository.factory.PasswordRepositoryFactory
import com.enekocm.securevault.data.storage.AppKeyStorage
import com.enekocm.securevault.data.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PasswordRepositoryModule {

    @Provides
    @Singleton
    fun providePasswordRepositoryFactory(
        firebasePasswordRepositoryProvider: Provider<FirebasePasswordRepository>,
        localPasswordRepositoryProvider: Provider<PasswordRepositoryImpl>,
        firebaseAuth: FirebaseAuth
    ): PasswordRepositoryFactory {
        return PasswordRepositoryFactory(
            firebasePasswordRepositoryProvider,
            localPasswordRepositoryProvider,
            firebaseAuth
        )
    }

    @Provides
    @Singleton
    fun provideFirebasePasswordRepository(
        storage: FirebaseStorage,
        passwordRepository: LocalPasswords,
        appKeyStorage: AppKeyStorage,
        fileEncryptor: FileEncryptor,
        firebaseAuth: FirebaseAuth
    ): FirebasePasswordRepository {
        return FirebasePasswordRepository(storage,passwordRepository,appKeyStorage,fileEncryptor,firebaseAuth)
    }

    @Provides
    fun providePasswordRepositoryImpl(
        storage: PasswordStorage,
        encryptor: FileEncryptor,
        localPasswords: LocalPasswords
    ): PasswordRepositoryImpl {
        return PasswordRepositoryImpl(storage, encryptor, localPasswords)
    }
}
