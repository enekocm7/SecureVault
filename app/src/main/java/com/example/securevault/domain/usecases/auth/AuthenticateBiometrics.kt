package com.example.securevault.domain.usecases.auth

import androidx.biometric.BiometricPrompt
import com.example.securevault.domain.biometric.BiometricAuthenticator
import javax.inject.Inject

class AuthenticateBiometrics @Inject constructor() {

    operator fun invoke(
        biometricAuthenticator: BiometricAuthenticator,
        title: String,
        description: String,
        cryptoObject: BiometricPrompt.CryptoObject?
    ) {
        biometricAuthenticator.showBiometricPrompt(title, description, cryptoObject)
    }
}