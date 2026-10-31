package domain.usecases.auth

import androidx.biometric.BiometricPrompt
import data.crypto.BiometricKeyManager
import javax.inject.Inject

class GetEncryptCryptoObject @Inject constructor() {
    operator fun invoke(): BiometricPrompt.CryptoObject? {
        return BiometricKeyManager.getEncryptCryptoObject()
    }
}