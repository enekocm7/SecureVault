package domain.usecases.auth

import domain.repository.MasterPasswordRepository
import javax.inject.Inject

class IsBiometricConfigured @Inject constructor(private val repo: MasterPasswordRepository) {
    operator fun invoke() = repo.isBiometricConfigured()
}