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
    private val encryptor: FileEncryptor,
    private val passwords: LocalPasswords
) : PasswordRepository {

    init {
        reloadPasswords()
    }

    override fun getAllPasswords(): List<Password> {
        return passwords.getAllPasswords()
    }

    private fun getAppKey(): String = runBlocking {
        String(AppKeyProvider.getAppKey())
    }


    override fun getPasswordByName(name: String): Password? {
        return passwords.getPasswordByName(name)
    }

    override fun getPasswordByNameContainingIgnoreCase(name: String): List<Password> {
        return passwords.getPasswordByNameContainingIgnoreCase(name)
    }

    override fun insertPassword(password: Password) {
        insertPassword(password.name, password)
    }

    override fun insertAllPasswords(passwords: List<Password>) {
        this.passwords.insertAllPasswords(passwords)
        savePasswordsFromCache()
    }

    override fun insertPassword(
        previousName: String,
        password: Password
    ) {
        passwords.insertPassword(previousName,password)
        val encryptedPasswords: String = encryptor.encryptPasswords(getAllPasswords(), getAppKey())
        storage.saveEncryptedFile(encryptedPasswords)
        reloadPasswords()
    }

    override fun deletePassword(password: Password) {
        passwords.deletePassword(password)
        val encryptedPasswords: String = encryptor.encryptPasswords(getAllPasswords(), getAppKey())
        storage.saveEncryptedFile(encryptedPasswords)
        reloadPasswords()
    }

    override fun deleteAllPasswords() {
        passwords.deleteAllPasswords()
        val encryptedPasswords: String = encryptor.encryptPasswords(getAllPasswords(), getAppKey())
        storage.saveEncryptedFile(encryptedPasswords)
        reloadPasswords()
    }

    private fun loadPasswords(): List<Password> {
        val encryptedPasswords = storage.readEncryptedFile()
        if (encryptedPasswords == null) return mutableListOf()
        return encryptor.decryptPasswords(encryptedPasswords, getAppKey())
    }

    private fun reloadPasswords() {
        passwords.insertAllPasswords(loadPasswords())
    }

    private fun savePasswordsFromCache(){
        storage.saveEncryptedFile(encryptor.encryptPasswords(getAllPasswords(),getAppKey()))
    }

}
