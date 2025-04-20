package com.example.securevault.domain.usecases

import androidx.biometric.BiometricPrompt
import com.example.securevault.data.crypto.BiometricKeyManager
import javax.inject.Inject

class GetEncryptCryptoObject @Inject constructor() {
    operator fun invoke(): BiometricPrompt.CryptoObject? {
        return BiometricKeyManager.getEncryptCryptoObject()
    }
}