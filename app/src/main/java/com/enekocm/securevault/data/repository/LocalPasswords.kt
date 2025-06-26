package com.enekocm.securevault.data.repository

import com.enekocm.securevault.data.json.model.Password
import com.enekocm.securevault.domain.repository.PasswordRepository
import javax.inject.Inject

class LocalPasswords @Inject constructor() : PasswordRepository {

    private var cachePasswords: MutableList<Password> = mutableListOf()

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
    }

    override fun insertAllPasswords(passwords: List<Password>) {
        passwords.forEach { insertPasswordCache(it.name, it) }
    }

    private fun insertPasswordCache(name: String, password: Password) {
        val existingIndex = cachePasswords.indexOfFirst { it.name == name }

        if (existingIndex >= 0) {
            cachePasswords[existingIndex] = password
        } else {
            cachePasswords.add(password)
        }
    }

    override fun deletePassword(password: Password) {
        cachePasswords.removeIf { it.name == password.name }
    }

    override fun deleteAllPasswords() {
        cachePasswords.clear()
    }
}