package com.example.securevault.data.crypto

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object AppKeyProvider {
    private var appKey: ByteArray? = null
    private val mutex = Mutex()

    suspend fun generate(): ByteArray = mutex.withLock {
        appKey?.copyOf() ?: ByteArray(32).apply {
            java.security.SecureRandom().nextBytes(this)
            appKey = this.copyOf()
        }
    }

    suspend fun load(decryptedKey: ByteArray) = mutex.withLock {
        require(decryptedKey.size == 32) { "Invalid key size" }
        appKey = decryptedKey.copyOf()
    }

    suspend fun getAppKey(): ByteArray = mutex.withLock {
        return appKey?.copyOf() ?: throw IllegalStateException("App key not loaded")
    }

}