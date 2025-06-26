package com.enekocm.securevault.data.repository.factory

import com.enekocm.securevault.data.repository.FirebasePasswordRepository
import com.enekocm.securevault.data.repository.PasswordRepositoryImpl
import com.enekocm.securevault.domain.repository.PasswordRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Provider

class PasswordRepositoryFactory @Inject constructor(
    private val firebasePasswordRepositoryProvider: Provider<FirebasePasswordRepository>,
    private val localPasswordRepositoryProvider: Provider<PasswordRepositoryImpl>,
    private val firebaseAuth: FirebaseAuth
) {

    fun getPasswordRepository(): PasswordRepository {
        return if (firebaseAuth.currentUser!=null) {
            firebasePasswordRepositoryProvider.get()
        } else {
            localPasswordRepositoryProvider.get()
        }
    }
}