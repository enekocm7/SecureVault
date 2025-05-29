package com.enekocm.securevault.domain.repository

import com.enekocm.securevault.domain.model.BiometricResult

interface MasterPasswordRepository {
    suspend fun generateAndStoreAppKey(password: String)
    suspend fun generateAndStoreAppKeyBio(result: BiometricResult)
    suspend fun unlockAppKeyWithPassword(password: String): Boolean
    suspend fun unlockAppKeyWithBiometrics(result: BiometricResult): Boolean
    fun isAppKeyConfigured(): Boolean
    fun isBiometricConfigured(): Boolean
    fun getIv(): ByteArray
}