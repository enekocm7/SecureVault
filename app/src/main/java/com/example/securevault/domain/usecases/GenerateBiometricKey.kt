package com.example.securevault.domain.usecases

import com.example.securevault.domain.repository.MasterPasswordRepository

class GenerateBiometricKey(private val repo: MasterPasswordRepository) {
    suspend operator fun invoke() = repo.generateAndStoreAppKeyBio()
}