package com.enekocm.securevault.data.autofill.handlers

import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import com.enekocm.securevault.data.autofill.utils.StructureParser
import com.enekocm.securevault.data.json.crypto.FileEncryptor
import com.enekocm.securevault.data.json.model.Password
import com.enekocm.securevault.data.json.storage.PasswordStorage
import com.enekocm.securevault.data.repository.PasswordRepositoryImpl
import javax.inject.Inject

class SaveRequestHandler @Inject constructor(
    private val storage: PasswordStorage,
    private val encryptor: FileEncryptor
) {

    fun handleSaveRequest(
        request: SaveRequest,
        callback: SaveCallback
    ) {
        val structure = request.fillContexts.lastOrNull()?.structure ?: run {
            return
        }

        val packageName = structure.activityComponent.packageName
        val credentials = StructureParser.extractCredentialsFromStructure(structure)

        if (credentials.isValid()) {
            try {
                val name = packageName.split('.').getOrNull(1)?.replaceFirstChar {
                    it.uppercase()
                } ?: packageName.replaceFirstChar { it.uppercase() }

                val password = Password(
                    name = name,
                    url = packageName,
                    username = credentials.username.toString(),
                    value = credentials.password.toString()
                )

                PasswordRepositoryImpl(storage, encryptor).insertPassword(password)
                callback.onSuccess()
            } catch (e: Exception) {
                callback.onFailure("Failed to show save password dialog: ${e.message}")
            }
        }
    }
}
