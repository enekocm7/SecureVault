package com.example.securevault.domain.usecases

import com.example.securevault.domain.biometric.BiometricAuthenticator
import javax.inject.Inject

class AuthenticateBiometrics @Inject constructor(private val biometricAuthenticator: BiometricAuthenticator) {

    operator fun invoke(
        title: String,
        description: String
    ) {
        biometricAuthenticator.showBiometricPrompt(title, description)
    }
}