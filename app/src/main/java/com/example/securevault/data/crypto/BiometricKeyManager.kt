package com.example.securevault.data.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricPrompt
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object BiometricKeyManager {
    private const val KEY_ALIAS = "biometric_key"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"

    fun generateKey(){
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        if (keyStore.containsAlias(KEY_ALIAS)) {
            return
        }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,ANDROID_KEYSTORE)
        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(true)
            .build()

        keyGenerator.init(keySpec)
        keyGenerator.generateKey()
    }

    private fun getKey() : SecretKey{
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    private fun getEncryptCipher(): Cipher {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        return cipher
    }

    private fun getDecryptCipher(iv: ByteArray): Cipher {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)
        return cipher
    }

    fun getEncryptCryptoObject(): BiometricPrompt.CryptoObject? {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            return null
        }
        val cipher = getEncryptCipher()
        return BiometricPrompt.CryptoObject(cipher)
    }

    fun getDecryptCryptoObject(iv: ByteArray): BiometricPrompt.CryptoObject {
        val cipher = getDecryptCipher(iv)
        return BiometricPrompt.CryptoObject(cipher)
    }

}