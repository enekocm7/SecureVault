package com.example.securevault.domain.model

import androidx.biometric.BiometricPrompt

sealed interface BiometricResult {
    data object HardwareNotAvailable : BiometricResult
    data object FeatureUnavailable : BiometricResult
    data class AuthenticationError(val error: String) : BiometricResult
    data object AuthenticationFailed : BiometricResult
    data class AuthenticationSuccess(val result: BiometricPrompt.AuthenticationResult? = null) : BiometricResult
    data object AuthenticationNotRecognized : BiometricResult

}