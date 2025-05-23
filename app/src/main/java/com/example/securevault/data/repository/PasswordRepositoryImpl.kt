package com.example.securevault.data.repository

import com.example.securevault.data.crypto.AppKeyProvider
import com.example.securevault.data.json.crypto.FileEncryptor
import com.example.securevault.data.json.model.Password
import com.example.securevault.data.json.storage.PasswordStorage
import com.example.securevault.domain.repository.PasswordRepository
import javax.inject.Inject

class PasswordRepositoryImpl @Inject constructor(
    private val storage: PasswordStorage,
    private val encryptor: FileEncryptor
) : PasswordRepository {

    private val appKey: String = String(AppKeyProvider.getAppKey())
    private lateinit var cachePasswords: MutableList<Password>

    init {
        reloadPasswords()
    }

    override fun getAllPasswords(): List<Password> {
        return cachePasswords
    }

    override fun getPasswordByName(name: String): Password? {
        return cachePasswords.find { it.name == name }
    }

    override fun getPasswordByNameContainingIgnoreCase(name: String): List<Password> {
        return cachePasswords.filter { it.name.contains(name, ignoreCase = true) }
    }

    override fun insertPassword(password: Password) {
        insertPassword(password.name, password)
        reloadPasswords()
    }

    override fun insertAllPasswords(passwords: List<Password>) {
        passwords.forEach { insertPasswordCache(it.name, it) }
        savePasswordsFromCache()
        reloadPasswords()
    }

    private fun insertPasswordCache(name: String, password: Password) {
        val existingIndex = cachePasswords.indexOfFirst { it.name == name }

        if (existingIndex >= 0) {
            cachePasswords[existingIndex] = password
        } else {
            cachePasswords.add(password)
        }
    }

    override fun insertPassword(
        previousName: String,
        password: Password
    ) {
        val existingIndex = cachePasswords.indexOfFirst { it.name == previousName }

        if (existingIndex >= 0) {
            cachePasswords[existingIndex] = password
        } else {
            cachePasswords.add(password)
        }
        val encryptedPasswords: String = encryptor.encryptPasswords(cachePasswords, appKey)
        storage.saveEncryptedFile(encryptedPasswords)
        reloadPasswords()
    }

    override fun deletePassword(password: Password) {
        cachePasswords.removeIf { it.name == password.name }
        val encryptedPasswords: String = encryptor.encryptPasswords(cachePasswords, appKey)
        storage.saveEncryptedFile(encryptedPasswords)
        reloadPasswords()
    }

    override fun deleteAllPasswords() {
        cachePasswords.clear()
        val encryptedPasswords: String = encryptor.encryptPasswords(cachePasswords, appKey)
        storage.saveEncryptedFile(encryptedPasswords)
        reloadPasswords()
    }

    private fun loadPasswords(): List<Password> {
        val encryptedPasswords = storage.readEncryptedFile()
        if (encryptedPasswords == null) return mutableListOf()
        return encryptor.decryptPasswords(encryptedPasswords, appKey)
    }

    fun reloadPasswords() {
        cachePasswords = loadPasswords().toMutableList()
    }

    private fun savePasswordsFromCache(){
        storage.saveEncryptedFile(encryptor.encryptPasswords(cachePasswords,appKey))
    }

}