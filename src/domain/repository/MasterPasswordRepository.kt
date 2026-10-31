package domain.repository

import domain.model.BiometricResult
import domain.model.Preferences

interface MasterPasswordRepository {
    suspend fun generateAndStoreAppKey(password: String)
    suspend fun generateAndStoreAppKeyBio(result: BiometricResult)
    suspend fun unlockAppKeyWithPassword(password: String): Boolean
    suspend fun unlockAppKeyWithFirebase(password: String, preferences: Preferences): Boolean
    suspend fun unlockAppKeyWithBiometrics(result: BiometricResult): Boolean
    fun isAppKeyConfigured(): Boolean
    fun isBiometricConfigured(): Boolean
    fun getIv(): ByteArray
}