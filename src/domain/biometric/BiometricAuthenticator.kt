package domain.biometric

import androidx.biometric.BiometricPrompt
import domain.model.BiometricResult
import kotlinx.coroutines.flow.Flow

interface BiometricAuthenticator {
    val promptResults: Flow<BiometricResult>
    fun showBiometricPrompt(
        title: String,
        description: String,
        cryptoObject: BiometricPrompt.CryptoObject? = null
    )
}