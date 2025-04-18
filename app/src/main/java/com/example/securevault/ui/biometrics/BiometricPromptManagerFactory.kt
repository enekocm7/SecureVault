package com.example.securevault.ui.biometrics

import androidx.appcompat.app.AppCompatActivity
import com.example.securevault.domain.biometric.BiometricAuthenticator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricPromptManagerFactory @Inject constructor() {
    fun create(activity: AppCompatActivity): BiometricAuthenticator {
        return BiometricPromptManager(activity)
    }
}