package com.enekocm.securevault.domain.usecases.auth

import androidx.biometric.BiometricPrompt
import com.enekocm.securevault.data.crypto.BiometricKeyManager
import com.enekocm.securevault.domain.repository.MasterPasswordRepository
import javax.inject.Inject

class GetDecryptCryptoObject @Inject constructor(
    private val masterPasswordRepository: MasterPasswordRepository
) {
    operator fun invoke(): BiometricPrompt.CryptoObject {
        val iv = masterPasswordRepository.getIv()
        return BiometricKeyManager.getDecryptCryptoObject(iv)
    }
}