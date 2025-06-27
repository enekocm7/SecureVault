package com.enekocm.securevault.domain.usecases.firestore

import com.enekocm.securevault.domain.model.Preferences
import com.enekocm.securevault.domain.repository.MasterPasswordRepository
import javax.inject.Inject

class UnlockKeyWithFirebase @Inject constructor(private val masterPasswordRepository: MasterPasswordRepository) {
    suspend operator fun invoke(password: String, preferences: Preferences): Boolean = masterPasswordRepository.unlockAppKeyWithFirebase(password,preferences)
}