package domain.usecases.auth

import androidx.biometric.BiometricPrompt
import data.crypto.BiometricKeyManager
import domain.repository.MasterPasswordRepository
import javax.inject.Inject

class GetDecryptCryptoObject @Inject constructor(
    private val masterPasswordRepository: MasterPasswordRepository
) {
    operator fun invoke(): BiometricPrompt.CryptoObject {
        val iv = masterPasswordRepository.getIv()
        return BiometricKeyManager.getDecryptCryptoObject(iv)
    }
}