package com.example.securevault.domain.repository

import com.example.securevault.domain.model.BiometricResult

interface MasterPasswordRepository {
    fun generateAndStoreAppKey(password: String)
    fun generateAndStoreAppKeyBio(result: BiometricResult)
    fun unlockAppKeyWithPassword(password: String): ByteArray?
    fun unlockAppKeyWithBiometrics(result: BiometricResult): ByteArray?
    fun isAppKeyConfigured(): Boolean
    fun isBiometricConfigured(): Boolean
    fun getIv(): ByteArray
}