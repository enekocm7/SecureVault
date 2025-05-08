package com.example.securevault.data.repository

import com.example.securevault.data.crypto.AppKeyEncryptor
import com.example.securevault.data.crypto.BiometricKeyManager
import com.example.securevault.data.crypto.PasswordKeyManager
import com.example.securevault.data.storage.AppKeyStorage
import com.example.securevault.domain.model.BiometricResult
import com.example.securevault.domain.repository.MasterPasswordRepository
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.inject.Singleton

@Singleton
class MasterPasswordRepositoryImpl(private val storage: AppKeyStorage) : MasterPasswordRepository {

    companion object {
        private val appKey = ByteArray(32).apply { SecureRandom().nextBytes(this) }
    }

    override fun generateAndStoreAppKey(password: String) {
        val salt = PasswordKeyManager.generateSalt()
        val passwordKey = PasswordKeyManager.deriveKey(password, salt)
        val (encWithPw, ivPw) = AppKeyEncryptor.encrypt(appKey, passwordKey)

        storage.save("salt", salt)
        storage.save("encrypted_app_key_pw", encWithPw)
        storage.save("iv_pw", ivPw)
    }

    override fun generateAndStoreAppKeyBio(result: BiometricResult) {
        BiometricKeyManager.generateKey()
        var cipher: Cipher? = null
        if (result is BiometricResult.AuthenticationSuccess) {
            cipher = result.result?.cryptoObject?.cipher ?: return
        }
        val (encWithBio, ivBio) = AppKeyEncryptor.encrypt(appKey, cipher)

        storage.save("encrypted_app_key_bio", encWithBio)
        storage.save("iv_bio", ivBio)
    }

    override fun unlockAppKeyWithPassword(password: String): ByteArray? {
        val salt = storage.getFromSharedPreferences("salt")
        val encryptedData = storage.getFromSharedPreferences("encrypted_app_key_pw")
        val iv = storage.getFromSharedPreferences("iv_pw")
        val passwordKey = PasswordKeyManager.deriveKey(password, salt)
        return try {
            AppKeyEncryptor.decrypt(encryptedData, passwordKey, iv)
        } catch (_: Exception) {
            null
        }
    }

    override fun unlockAppKeyWithBiometrics(result: BiometricResult): ByteArray? {
        if (result !is BiometricResult.AuthenticationSuccess) {
            return null
        }
        val encryptedData = storage.getFromSharedPreferences("encrypted_app_key_bio")
        val authenticatedCipher = result.result?.cryptoObject?.cipher ?: return null

        return try {
            AppKeyEncryptor.decrypt(encryptedData, authenticatedCipher)
        } catch (_: Exception) {
            null
        }
    }

    override fun isAppKeyConfigured(): Boolean {
        return storage.isPasswordConfigured()
    }

    override fun isBiometricConfigured(): Boolean {
        return storage.isBiometricConfigured()
    }

    override fun getIv(): ByteArray {
        return storage.getFromSharedPreferences("iv_bio")
    }


}