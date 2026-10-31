package domain.usecases.auth

import domain.model.BiometricResult
import domain.repository.MasterPasswordRepository
import javax.inject.Inject

class UnlockKeyWithBiometrics @Inject constructor(private val repo: MasterPasswordRepository) {
    suspend operator fun invoke(result: BiometricResult): Boolean = repo.unlockAppKeyWithBiometrics(result)

}