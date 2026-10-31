package di

import data.json.crypto.FileEncryptor
import data.json.storage.PasswordStorage
import data.repository.FirebasePasswordRepository
import data.repository.LocalPasswords
import data.repository.PasswordRepositoryImpl
import data.repository.factory.PasswordRepositoryFactory
import data.storage.AppKeyStorage
import data.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Provider
import javax.inject.Singleton

object PasswordRepositoryModule {

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

    fun providePasswordRepositoryImpl(
        storage: PasswordStorage,
        encryptor: FileEncryptor,
        localPasswords: LocalPasswords
    ): PasswordRepositoryImpl {
        return PasswordRepositoryImpl(storage, encryptor, localPasswords)
    }
}
