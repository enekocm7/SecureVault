package data.repository.factory

import data.repository.FirebasePasswordRepository
import data.repository.PasswordRepositoryImpl
import domain.repository.PasswordRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Provider

class PasswordRepositoryFactory @Inject constructor(
    private val firebasePasswordRepositoryProvider: Provider<FirebasePasswordRepository>,
    private val localPasswordRepositoryProvider: Provider<PasswordRepositoryImpl>,
    private val firebaseAuth: FirebaseAuth?
) {

    fun getPasswordRepository(): PasswordRepository {
        return if (firebaseAuth?.currentUser != null) {
            firebasePasswordRepositoryProvider.get()
        } else {
            localPasswordRepositoryProvider.get()
        }
    }
}
