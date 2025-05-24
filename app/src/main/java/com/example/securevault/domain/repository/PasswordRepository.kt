package com.example.securevault.domain.repository

import com.example.securevault.data.json.model.Password

interface PasswordRepository {
    fun getAllPasswords(): List<Password>
    fun getPasswordByName(name: String): Password?
    fun getPasswordByNameContainingIgnoreCase(name: String): List<Password>
    fun insertPassword(password: Password)
    fun insertPassword(previousName: String, password: Password)
    fun insertAllPasswords(passwords: List<Password>)
    fun deletePassword(password: Password)
    fun deleteAllPasswords()
}