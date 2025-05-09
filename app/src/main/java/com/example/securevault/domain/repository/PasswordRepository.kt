package com.example.securevault.domain.repository

import com.example.securevault.data.json.model.Password

interface PasswordRepository {
    fun getAllPasswords(): List<Password>
    fun getPasswordByName(name: String): Password?
    fun getPasswordByNameContainingIgnoreCase(name: String): List<Password>
    fun insertPassword(password: Password)
    fun deletePassword(password: Password)
}