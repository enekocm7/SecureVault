package com.example.securevault.domain.usecases.auth

import com.example.securevault.domain.model.BiometricResult
import com.example.securevault.domain.repository.MasterPasswordRepository
import javax.inject.Inject

class UnlockKeyWithBiometrics @Inject constructor(private val repo: MasterPasswordRepository) {
    suspend operator fun invoke(result: BiometricResult): Boolean = repo.unlockAppKeyWithBiometrics(result)

}