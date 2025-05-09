package com.example.securevault.domain.repository

import com.example.securevault.data.json.model.Password

interface PasswordRepository {
    fun getAllPasswords(): List<Password>
    fun getPasswordByName(): Password
    fun getPasswordByNameContainingIgnoreCase(): List<Password>
    fun insertPassword(password: Password): Boolean
    fun deletePassword(password: Password): Boolean
}