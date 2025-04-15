package com.example.securevault.ui.biometrics

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt.PromptInfo

class BiometricPrompManager(
    private val activity: AppCompatActivity
) {
    fun showBiometricPrompt(title:String,description: String){
        val manager = BiometricManager.from(activity)
        val authenticators = BIOMETRIC_STRONG or DEVICE_CREDENTIAL

        val promptInfo = PromptInfo.Builder()
            .setTitle(title)

    }

}