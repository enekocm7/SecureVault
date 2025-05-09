package com.example.securevault.domain.usecases.auth

import androidx.biometric.BiometricPrompt
import com.example.securevault.data.crypto.BiometricKeyManager
import com.example.securevault.domain.repository.MasterPasswordRepository
import javax.inject.Inject

class GetDecryptCryptoObject @Inject constructor(
    private val masterPasswordRepository: MasterPasswordRepository
) {
    operator fun invoke(): BiometricPrompt.CryptoObject {
        val iv = masterPasswordRepository.getIv()
        return BiometricKeyManager.getDecryptCryptoObject(iv)
    }
}