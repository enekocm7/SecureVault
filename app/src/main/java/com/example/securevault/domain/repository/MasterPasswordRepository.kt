package com.example.securevault.domain.repository

import com.example.securevault.data.storage.AppKeyStorage

interface MasterPasswordRepository {
    suspend fun generateAndStoreAppKey(password: String)
    suspend fun generateAndStoreAppKeyBio()
    suspend fun unlockAppKeyWithPassword(password: String): ByteArray?
    suspend fun unlockAppKeyWithBiometrics(): ByteArray?
    suspend fun isAppKeyConfigured(): Boolean
    suspend fun isBiometricConfigured(): Boolean
}