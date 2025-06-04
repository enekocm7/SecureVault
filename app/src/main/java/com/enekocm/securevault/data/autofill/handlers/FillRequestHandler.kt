package com.enekocm.securevault.data.autofill.handlers

import android.content.Context
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.view.autofill.AutofillValue
import com.enekocm.securevault.R
import com.enekocm.securevault.data.autofill.Fetch
import com.enekocm.securevault.data.autofill.StructureParser
import com.enekocm.securevault.data.autofill.entities.ParsedStructure
import com.enekocm.securevault.data.autofill.utils.Utils
import com.enekocm.securevault.data.autofill.utils.Utils.isAppKeyAvailable
import com.enekocm.securevault.data.json.crypto.FileEncryptor
import com.enekocm.securevault.data.json.storage.PasswordStorage
import com.enekocm.securevault.data.repository.PasswordRepositoryImpl
import com.enekocm.securevault.domain.repository.PasswordRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FillRequestHandler @Inject constructor(@ApplicationContext private val context: Context) {

    fun handleFillRequest(
        request: FillRequest,
        callback: FillCallback,
        storage: PasswordStorage,
        encryptor: FileEncryptor
    ) {
        val structure = request.fillContexts.lastOrNull()?.structure ?: return
        val packageName = structure.activityComponent.packageName
        val parsedStructure = StructureParser.parseStructure(structure) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            if (!isAppKeyAvailable()) {
                handleUnauthenticatedFill(request, packageName, parsedStructure, callback)
                return@launch
            }


            handleAuthenticatedFill(
                request,
                packageName,
                parsedStructure,
                callback,
                PasswordRepositoryImpl(storage, encryptor)
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun handleUnauthenticatedFill(
        request: FillRequest,
        packageName: String,
        parsedStructure: ParsedStructure,
        callback: FillCallback
    ) {
        val loginInlinePresentation = Utils.createInlinePresentation(
            context,
            request.inlineSuggestionsRequest,
            context.getString(R.string.login_to_secure_vault)
        )
        val loginIntent = Utils.createLoginIntent(
            context,
            packageName,
            parsedStructure.usernameId,
            parsedStructure.passwordId
        ).intentSender

        if (loginInlinePresentation != null) {
            val dataset = Dataset.Builder()
                .setInlinePresentation(loginInlinePresentation)
                .setValue(
                    parsedStructure.usernameId,
                    AutofillValue.forText(null),
                )
                .setValue(
                    parsedStructure.passwordId,
                    AutofillValue.forText(null),
                )
                .setAuthentication(
                    loginIntent
                )
                .build()

            val response = FillResponse.Builder()
                .addDataset(dataset)
                .build()

            callback.onSuccess(response)

        } else {
            callback.onSuccess(null)
        }
    }

    @Suppress("DEPRECATION")
    private fun handleAuthenticatedFill(
        request: FillRequest,
        packageName: String,
        parsedStructure: ParsedStructure,
        callback: FillCallback,
        passwordRepository: PasswordRepository?
    ) {
        if (passwordRepository == null) {
            callback.onSuccess(null)
            return
        }

        val credentials = Fetch.fetchPassword(
            packageName,
            passwordRepository.getAllPasswords()
        ) ?: return

        val inlinePresentation = Utils.createInlinePresentation(
            context,
            request.inlineSuggestionsRequest,
            credentials.username ?: return
        ) ?: run {
            callback.onSuccess(null)
            return
        }

        if (credentials.isValid()) {
            val dataset = Dataset.Builder()
                .setValue(
                    parsedStructure.usernameId,
                    AutofillValue.forText(credentials.username)
                )
                .setValue(
                    parsedStructure.passwordId,
                    AutofillValue.forText(credentials.password)
                )
                .setInlinePresentation(inlinePresentation)
                .build()

            val fillResponse = FillResponse.Builder()
                .addDataset(dataset)
                .setSaveInfo(
                    Utils.getSaveInfo(parsedStructure)
                ).build()

            callback.onSuccess(fillResponse)
        } else {
            callback.onSuccess(
                FillResponse.Builder()
                    .setSaveInfo(
                        Utils.getSaveInfo(parsedStructure)
                    ).build()
            )
        }
    }
}
