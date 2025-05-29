package com.enekocm.securevault.domain.usecases.auth

import com.enekocm.securevault.domain.model.BiometricResult
import com.enekocm.securevault.domain.repository.MasterPasswordRepository
import javax.inject.Inject

class GenerateBiometricKey @Inject constructor(private val repo: MasterPasswordRepository) {
    suspend operator fun invoke(result: BiometricResult) = repo.generateAndStoreAppKeyBio(result)
}