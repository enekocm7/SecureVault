package com.example.securevault.data.repository

import com.example.securevault.data.json.crypto.FileEncryptor
import com.example.securevault.data.json.model.Password
import com.example.securevault.data.json.storage.PasswordStorage
import com.example.securevault.domain.repository.PasswordRepository
import javax.inject.Inject

class PasswordRepositoryImpl @Inject constructor(
    private val password: PasswordStorage,
    private val encryptor: FileEncryptor
) : PasswordRepository {

    override fun getAllPasswords(): List<Password> {
        TODO("Not yet implemented")
    }

    override fun getPasswordByName(): Password {
        TODO("Not yet implemented")
    }

    override fun getPasswordByNameContainingIgnoreCase(): List<Password> {
        TODO("Not yet implemented")
    }

    override fun insertPassword(password: Password): Boolean {
        TODO("Not yet implemented")
    }

    override fun deletePassword(password: Password): Boolean {
        TODO("Not yet implemented")
    }

}