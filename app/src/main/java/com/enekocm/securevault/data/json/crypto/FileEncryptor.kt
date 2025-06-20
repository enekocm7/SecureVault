package com.enekocm.securevault.data.json.crypto

import android.util.Base64
import com.enekocm.securevault.data.crypto.AppKeyEncryptor
import com.enekocm.securevault.data.crypto.PasswordKeyManager
import com.enekocm.securevault.data.json.model.Password

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.crypto.SecretKey
import javax.inject.Inject

class FileEncryptor @Inject constructor(moshi: Moshi) {

    private val type = Types.newParameterizedType(List::class.java, Password::class.java)
    private val adapter: JsonAdapter<List<Password>> = moshi.adapter(type)

    private fun encryptFile(content: String, password: String): String {
        val salt = PasswordKeyManager.generateSalt()

        val secretKey = PasswordKeyManager.deriveKey(password, salt)

        val (encryptedData, iv) = AppKeyEncryptor.encrypt(content.toByteArray(), secretKey)

        val combined = ByteArray(salt.size + iv.size + encryptedData.size)
        System.arraycopy(salt, 0, combined, 0, salt.size)
        System.arraycopy(iv, 0, combined, salt.size, iv.size)
        System.arraycopy(encryptedData, 0, combined, salt.size + iv.size, encryptedData.size)

        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    private fun decryptFile(encryptedFile: String, password: String): String {
        if (encryptedFile.isEmpty()) {
            return ""
        }

        try {
            val combined = Base64.decode(encryptedFile, Base64.DEFAULT)

            val salt = ByteArray(16)
            System.arraycopy(combined, 0, salt, 0, salt.size)

            val iv = ByteArray(12)
            System.arraycopy(combined, salt.size, iv, 0, iv.size)

            val encryptedData = ByteArray(combined.size - salt.size - iv.size)
            System.arraycopy(combined, salt.size + iv.size, encryptedData, 0, encryptedData.size)

            val secretKey: SecretKey = PasswordKeyManager.deriveKey(password, salt)
            return String(AppKeyEncryptor.decrypt(encryptedData, secretKey, iv))
        } catch (_: Exception) {
            throw IllegalArgumentException("Failed to decrypt")
        }
    }

    fun encryptPasswords(passwords: List<Password>, userPassword: String): String {
        val parsedJson = adapter.toJson(passwords)
        return encryptFile(parsedJson, userPassword)
    }

    fun decryptPasswords(encryptedPasswords: String, userPassword: String): List<Password> {
        val decryptedJson = decryptFile(encryptedPasswords, userPassword)
        var passwords: List<Password>
        try {
            passwords = adapter.fromJson(decryptedJson) ?: emptyList()
        } catch (_: Exception) {
            return emptyList()
        }
        return passwords
    }
}