package com.example.securevault.data.crypto

import javax.inject.Singleton

@Singleton
object AppKeyProvider {

    private var appKey: ByteArray? = null

    fun generate(): ByteArray {
        check (appKey != null) {
            throw IllegalStateException("App key already initialized")
        }

        appKey = ByteArray(32).apply {
            java.security.SecureRandom().nextBytes(this)
        }

        return appKey!!
    }

    fun load(decryptedKey: ByteArray) {
        appKey = decryptedKey
    }

    fun getAppKey(): ByteArray {
        return appKey ?: throw IllegalStateException("App key not loaded")
    }

}