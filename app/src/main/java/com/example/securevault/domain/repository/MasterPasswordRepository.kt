package com.example.securevault.domain.repository

interface MasterPasswordRepository {
    fun generateAndStoreAppKey(password: String)
    fun generateAndStoreAppKeyBio()
    fun unlockAppKeyWithPassword(password: String): ByteArray?
    fun unlockAppKeyWithBiometrics(): ByteArray?
    fun isAppKeyConfigured(): Boolean
    fun isBiometricConfigured(): Boolean
}