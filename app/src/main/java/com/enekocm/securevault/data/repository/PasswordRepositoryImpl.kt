package com.enekocm.securevault.data.repository

import com.enekocm.securevault.data.crypto.AppKeyProvider
import com.enekocm.securevault.data.json.crypto.FileEncryptor
import com.enekocm.securevault.data.json.model.Password
import com.enekocm.securevault.data.json.storage.PasswordStorage
import com.enekocm.securevault.domain.repository.PasswordRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class PasswordRepositoryImpl @Inject constructor(
    private val storage: PasswordStorage,
    private val encryptor: FileEncryptor
) : PasswordRepository {

    private lateinit var cachePasswords: MutableList<Password>

    init {
        runBlocking {
            reloadPasswords()
        }
    }

    override fun getAllPasswords(): List<Password> {
        return cachePasswords
    }

    private fun getAppKey(): String = runBlocking {
        String(AppKeyProvider.getAppKey())
    }


    override fun getPasswordByName(name: String): Password? {
        return cachePasswords.find { it.name == name }
    }

    override fun getPasswordByNameContainingIgnoreCase(name: String): List<Password> {
        return cachePasswords.filter { it.name.contains(name, ignoreCase = true) }
    }

    override fun insertPassword(password: Password) {
        insertPassword(password.name, password)
    }

    override fun insertAllPasswords(passwords: List<Password>) {
        passwords.forEach { insertPasswordCache(it.name, it) }
        savePasswordsFromCache()
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
        val encryptedPasswords: String = encryptor.encryptPasswords(cachePasswords, getAppKey())
        storage.saveEncryptedFile(encryptedPasswords)
        reloadPasswords()
    }

    override fun deletePassword(password: Password) {
        cachePasswords.removeIf { it.name == password.name }
        val encryptedPasswords: String = encryptor.encryptPasswords(cachePasswords, getAppKey())
        storage.saveEncryptedFile(encryptedPasswords)
        reloadPasswords()
    }

    override fun deleteAllPasswords() {
        cachePasswords.clear()
        val encryptedPasswords: String = encryptor.encryptPasswords(cachePasswords, getAppKey())
        storage.saveEncryptedFile(encryptedPasswords)
        reloadPasswords()
    }

    private fun loadPasswords(): List<Password> {
        val encryptedPasswords = storage.readEncryptedFile()
        if (encryptedPasswords == null) return mutableListOf()
        return encryptor.decryptPasswords(encryptedPasswords, getAppKey())
    }

    fun reloadPasswords() {
        cachePasswords = loadPasswords().toMutableList()
    }

    private fun savePasswordsFromCache(){
        storage.saveEncryptedFile(encryptor.encryptPasswords(cachePasswords,getAppKey()))
    }

}
