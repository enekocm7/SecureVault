package com.example.securevault.domain.usecases

import com.example.securevault.domain.model.BiometricResult
import com.example.securevault.domain.repository.MasterPasswordRepository
import javax.inject.Inject

class GenerateBiometricKey @Inject constructor(private val repo: MasterPasswordRepository) {
     operator fun invoke(result: BiometricResult) = repo.generateAndStoreAppKeyBio(result)
}