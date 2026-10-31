package domain.usecases.auth

import domain.repository.MasterPasswordRepository
import javax.inject.Inject

class UnlockKeyWithPassword @Inject constructor(private val repo: MasterPasswordRepository) {
    suspend operator fun invoke(password: String): Boolean = repo.unlockAppKeyWithPassword(password)
}