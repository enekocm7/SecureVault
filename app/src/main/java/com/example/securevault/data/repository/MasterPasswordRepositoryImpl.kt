package com.example.securevault.data.repository

import com.example.securevault.data.crypto.AppKeyEncryptor
import com.example.securevault.data.crypto.BiometricKeyManager
import com.example.securevault.data.crypto.PasswordKeyManager
import com.example.securevault.data.storage.AppKeyStorage
import com.example.securevault.domain.repository.MasterPasswordRepository
import java.security.SecureRandom

class MasterPasswordRepositoryImpl(private val storage: AppKeyStorage) : MasterPasswordRepository{

    companion object{
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

    override fun generateAndStoreAppKeyBio() {
        BiometricKeyManager.generateKey()
        val biometricKey = BiometricKeyManager.getKey()
        val (encryptedWithBio, ivBio) = AppKeyEncryptor.encrypt(appKey, biometricKey)

        storage.save("encrypted_app_key_bio", encryptedWithBio)
        storage.save("iv_bio", ivBio)
    }

    override fun unlockAppKeyWithPassword(password: String): ByteArray? {
        val salt = storage.get("salt")?: return null
        val encryptedData = storage.get("encrypted_app_key_pw") ?: return null
        val iv = storage.get("iv_pw")?: return null
        val passwordKey = PasswordKeyManager.deriveKey(password, salt)
        return try {
            AppKeyEncryptor.decrypt(encryptedData, passwordKey, iv)
        }catch (_: Exception){
            null
        }

    }

    override fun unlockAppKeyWithBiometrics(): ByteArray? {
        val encryptedData = storage.get("encrypted_app_key_bio") ?: return null
        val iv = storage.get("iv_bio")?: return null
        val biometricKey = BiometricKeyManager.getKey()
        return try {
            AppKeyEncryptor.decrypt(encryptedData, biometricKey, iv)
        }catch (_: Exception){
            null
        }
    }

    override fun isAppKeyConfigured(): Boolean {
        return storage.isPasswordConfigured()
    }

    override fun isBiometricConfigured(): Boolean {
        return storage.isBiometricConfigured()
    }

}