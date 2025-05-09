package com.example.securevault.data.repository

import com.example.securevault.data.crypto.AppKeyEncryptor
import com.example.securevault.data.crypto.AppKeyProvider
import com.example.securevault.data.crypto.BiometricKeyManager
import com.example.securevault.data.crypto.PasswordKeyManager
import com.example.securevault.data.storage.AppKeyStorage
import com.example.securevault.domain.model.BiometricResult
import com.example.securevault.domain.repository.MasterPasswordRepository
import javax.crypto.Cipher
import javax.inject.Singleton

@Singleton
class MasterPasswordRepositoryImpl(private val storage: AppKeyStorage) : MasterPasswordRepository {

    override fun generateAndStoreAppKey(password: String) {
        val appKey = AppKeyProvider.generate()
        AppKeyProvider.load(appKey)
        val salt = PasswordKeyManager.generateSalt()
        val passwordKey = PasswordKeyManager.deriveKey(password, salt)
        val (encWithPw, ivPw) = AppKeyEncryptor.encrypt(appKey, passwordKey)

        storage.save("salt", salt)
        storage.save("encrypted_app_key_pw", encWithPw)
        storage.save("iv_pw", ivPw)
    }

    override fun generateAndStoreAppKeyBio(result: BiometricResult) {
        val appKey = AppKeyProvider.get()
        BiometricKeyManager.generateKey()
        var cipher: Cipher? = null
        if (result is BiometricResult.AuthenticationSuccess) {
            cipher = result.result?.cryptoObject?.cipher ?: return
        }
        val (encWithBio, ivBio) = AppKeyEncryptor.encrypt(appKey, cipher)

        storage.save("encrypted_app_key_bio", encWithBio)
        storage.save("iv_bio", ivBio)
    }

    override fun unlockAppKeyWithPassword(password: String): Boolean {
        val salt = storage.getFromSharedPreferences("salt")
        val encryptedData = storage.getFromSharedPreferences("encrypted_app_key_pw")
        val iv = storage.getFromSharedPreferences("iv_pw")
        val passwordKey = PasswordKeyManager.deriveKey(password, salt)
        try{
            val appKey = AppKeyEncryptor.decrypt(encryptedData, passwordKey, iv)
            AppKeyProvider.load(appKey)
            return true
        }catch (_: Exception){
            return false
        }

    }

    override fun unlockAppKeyWithBiometrics(result: BiometricResult): Boolean {
        if (result !is BiometricResult.AuthenticationSuccess) {
            return false
        }
        val encryptedData = storage.getFromSharedPreferences("encrypted_app_key_bio")
        val authenticatedCipher = result.result?.cryptoObject?.cipher ?: return false

        try{
            val appKey = AppKeyEncryptor.decrypt(encryptedData, authenticatedCipher)
            AppKeyProvider.load(appKey)
            return true
        }catch (_: Exception){
            return false
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