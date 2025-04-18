package com.example.securevault.domain.usecases

import com.example.securevault.domain.biometric.BiometricAuthenticator
import javax.inject.Inject

class AuthenticateBiometrics @Inject constructor() {

    operator fun invoke(
        biometricAuthenticator: BiometricAuthenticator,
        title: String,
        description: String
    ) {
        biometricAuthenticator.showBiometricPrompt(title, description)
    }
}