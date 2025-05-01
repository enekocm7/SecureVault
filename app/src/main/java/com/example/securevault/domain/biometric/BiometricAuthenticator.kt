package com.example.securevault.domain.biometric

import androidx.biometric.BiometricPrompt
import com.example.securevault.domain.model.BiometricResult
import kotlinx.coroutines.flow.Flow

interface BiometricAuthenticator {
    val promptResults : Flow<BiometricResult>
    fun showBiometricPrompt(title: String, description: String, cryptoObject: BiometricPrompt.CryptoObject? = null)
}