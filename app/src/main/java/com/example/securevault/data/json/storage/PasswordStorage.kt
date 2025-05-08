package com.example.securevault.data.json.storage

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class PasswordStorage @Inject constructor(@ApplicationContext private val context: Context) {

    private val fileName = "passwords.sv"

    fun saveEncryptedFile(encryptedFile: String) {
        val file = File(context.filesDir, fileName)
        file.writeText(encryptedFile)
    }

    fun readEncryptedFile(): String? {
        val file = File(context.filesDir, fileName)

        return if (file.exists()) {
            file.readText(Charsets.UTF_8)
        } else {
            null
        }
    }

}