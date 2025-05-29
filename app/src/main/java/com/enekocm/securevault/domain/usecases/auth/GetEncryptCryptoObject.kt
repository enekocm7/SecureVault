package com.enekocm.securevault.domain.usecases.auth

import androidx.biometric.BiometricPrompt
import com.enekocm.securevault.data.crypto.BiometricKeyManager
import javax.inject.Inject

class GetEncryptCryptoObject @Inject constructor() {
    operator fun invoke(): BiometricPrompt.CryptoObject? {
        return BiometricKeyManager.getEncryptCryptoObject()
    }
}