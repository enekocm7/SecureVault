package com.example.securevault.domain.model

sealed interface BiometricResult {
    data object HardwareNotAvailable : BiometricResult
    data object FeatureUnavailable : BiometricResult
    data class AuthenticationError(val error: String) : BiometricResult
    data object AuthenticationFailed : BiometricResult
    data object AuthenticationSuccess : BiometricResult
    data object AuthenticationNotRecognized : BiometricResult

}