package com.enekocm.securevault.data.repository

import com.enekocm.securevault.data.crypto.AppKeyProvider
import com.enekocm.securevault.data.firestore.FirestoreModel
import com.enekocm.securevault.data.json.crypto.FileEncryptor
import com.enekocm.securevault.data.json.model.Password
import com.enekocm.securevault.data.storage.AppKeyStorage
import com.enekocm.securevault.data.storage.FirebaseStorage
import com.enekocm.securevault.domain.repository.PasswordRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Blob
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.collections.emptyList

class FirebasePasswordRepository @Inject constructor(
    private val storage: FirebaseStorage,
    private val passwordRepository: LocalPasswords,
    private val appKeyStorage: AppKeyStorage,
    private val fileEncryptor: FileEncryptor,
    private val firebaseAuth: FirebaseAuth
) : PasswordRepository {

    init {
        runBlocking {
            reloadPasswords()
        }
    }

    override fun getAllPasswords(): List<Password> {
        return passwordRepository.getAllPasswords()
    }

    override fun getPasswordByName(name: String): Password? {
        return passwordRepository.getPasswordByName(name)
    }

    override fun getPasswordByNameContainingIgnoreCase(name: String): List<Password> {
        return passwordRepository.getPasswordByNameContainingIgnoreCase(name)
    }

    override fun insertPassword(password: Password) {
        passwordRepository.insertPassword(password)
        savePasswords()
    }

    override fun insertPassword(
        previousName: String,
        password: Password
    ) {
        passwordRepository.insertPassword(previousName, password)
        savePasswords()
    }

    override fun insertAllPasswords(passwords: List<Password>) {
        passwordRepository.insertAllPasswords(passwords)
        savePasswords()
    }

    override fun deletePassword(password: Password) {
        passwordRepository.deletePassword(password)
        savePasswords()
    }

    override fun deleteAllPasswords() {
        passwordRepository.deleteAllPasswords()
        savePasswords()
    }

    private fun passwordsToModel(passwords: List<Password>): FirestoreModel {
        val key = Blob.fromBytes(appKeyStorage.getFromSharedPreferences("encrypted_app_key_pw"))
        val salt = Blob.fromBytes(appKeyStorage.getFromSharedPreferences("salt"))
        val iv = Blob.fromBytes(appKeyStorage.getFromSharedPreferences("iv_pw"))
        val passwords = fileEncryptor.encryptPasswords(passwords, getAppKey())
        val uid = firebaseAuth.uid ?: ""
        return FirestoreModel(
            uid = uid,
            derivedKey = key,
            salt = salt,
            iv = iv,
            passwords = passwords
        )
    }

    private fun modelToPasswords(model: FirestoreModel): List<Password> {
        return fileEncryptor.decryptPasswords(model.passwords, getAppKey())
    }

    private fun savePasswords() {
        storage.savePasswords(passwordsToModel(getAllPasswords()))
    }

    private fun loadPasswords(): List<Password> {
        return runBlocking {
            val model = storage.getPasswords(firebaseAuth.uid) ?: return@runBlocking emptyList()
            modelToPasswords(model)
        }
    }

    fun reloadPasswords() {
        passwordRepository.insertAllPasswords(loadPasswords())
    }


    private fun getAppKey(): String = runBlocking {
        String(AppKeyProvider.getAppKey())
    }
}