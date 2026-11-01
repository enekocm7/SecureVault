package di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import data.json.crypto.FileEncryptor
import data.json.storage.PasswordStorage
import data.repository.FirebasePasswordRepository
import data.repository.LocalPasswords
import data.repository.PasswordRepositoryImpl
import data.repository.factory.PasswordRepositoryFactory
import data.storage.AppKeyStorage
import data.storage.FirebaseStorage
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
        firebaseAuth: FirebaseAuth?
    ): PasswordRepositoryFactory {
        return PasswordRepositoryFactory(
            firebasePasswordRepositoryProvider,
            localPasswordRepositoryProvider,
            firebaseAuth
        )
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(db: FirebaseFirestore?): FirebaseStorage? {
        return db?.let { FirebaseStorage(it) }
    }

    @Provides
    fun provideFirebasePasswordRepository(
        storage: FirebaseStorage?,
        passwordRepository: LocalPasswords,
        appKeyStorage: AppKeyStorage,
        fileEncryptor: FileEncryptor,
        firebaseAuth: FirebaseAuth?
    ): FirebasePasswordRepository {
        // Only ever called when Firebase is initialized: PasswordRepositoryFactory
        // calls this provider solely when firebaseAuth?.currentUser != null, which
        // implies FirebaseApp exists, so both are non-null here.
        return FirebasePasswordRepository(
            checkNotNull(storage),
            passwordRepository,
            appKeyStorage,
            fileEncryptor,
            checkNotNull(firebaseAuth)
        )
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
