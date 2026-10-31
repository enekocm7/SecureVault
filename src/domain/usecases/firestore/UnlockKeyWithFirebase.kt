package domain.usecases.firestore

import domain.model.Preferences
import domain.repository.MasterPasswordRepository
import javax.inject.Inject

class UnlockKeyWithFirebase @Inject constructor(private val masterPasswordRepository: MasterPasswordRepository) {
    suspend operator fun invoke(password: String, preferences: Preferences): Boolean = masterPasswordRepository.unlockAppKeyWithFirebase(password,preferences)
}