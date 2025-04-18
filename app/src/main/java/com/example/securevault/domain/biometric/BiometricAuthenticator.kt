package com.example.securevault.domain.biometric

import com.example.securevault.domain.model.BiometricResult
import kotlinx.coroutines.flow.Flow

interface BiometricAuthenticator {
    fun showBiometricPrompt(title: String, description: String)
    val promptResults : Flow<BiometricResult>
}