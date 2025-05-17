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

    private val appKey: String = String(AppKeyProvider.get())
    private lateinit var cachePasswords: MutableList<Password>

    init {
        reloadPasswords()
    }

    override fun getAllPasswords(): List<Password> {
        reloadPasswords()
        return cachePasswords
    }

    override fun getPasswordByName(name: String): Password? {
        reloadPasswords()
        return cachePasswords.find { it.name == name }
    }

    override fun getPasswordByNameContainingIgnoreCase(name: String): List<Password> {
        reloadPasswords()
        return cachePasswords.filter { it.name.contains(name, ignoreCase = true) }
    }

    override fun insertPassword(password: Password) {
        insertPassword(password.name, password)
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
    }

    override fun deletePassword(password: Password) {
        cachePasswords.removeIf { it.name == password.name }
        val encryptedPasswords: String = encryptor.encryptPasswords(cachePasswords, appKey)
        storage.saveEncryptedFile(encryptedPasswords)
    }

    private fun loadPasswords(): List<Password> {
        val encryptedPasswords = storage.readEncryptedFile()
        if (encryptedPasswords == null) return mutableListOf<Password>()
        return encryptor.decryptPasswords(encryptedPasswords, appKey)
    }

    fun reloadPasswords() {
        cachePasswords = loadPasswords().toMutableList()
    }

}