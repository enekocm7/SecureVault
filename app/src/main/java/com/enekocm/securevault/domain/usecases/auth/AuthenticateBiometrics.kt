package com.enekocm.securevault.domain.usecases.auth

import androidx.biometric.BiometricPrompt
import com.enekocm.securevault.domain.biometric.BiometricAuthenticator
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