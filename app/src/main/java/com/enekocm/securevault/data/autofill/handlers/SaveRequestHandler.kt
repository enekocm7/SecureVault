package com.enekocm.securevault.data.autofill.handlers

import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import com.enekocm.securevault.data.autofill.StructureParser
import com.enekocm.securevault.data.json.model.Password
import com.enekocm.securevault.domain.repository.PasswordRepository

class SaveRequestHandler {

    fun handleSaveRequest(
        request: SaveRequest,
        callback: SaveCallback,
        passwordRepository: PasswordRepository?
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

                passwordRepository?.insertPassword(password)
                callback.onSuccess()
            } catch (e: Exception) {
                callback.onFailure("Failed to show save password dialog: ${e.message}")
            }
        }
    }
}
