package domain.repository

import data.json.model.Password

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