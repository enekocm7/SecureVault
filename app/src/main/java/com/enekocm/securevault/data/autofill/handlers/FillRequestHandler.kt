package com.enekocm.securevault.data.autofill.handlers

import android.app.assist.AssistStructure
import android.content.Context
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.view.autofill.AutofillValue
import com.enekocm.securevault.data.autofill.Fetch
import com.enekocm.securevault.data.autofill.StructureParser
import com.enekocm.securevault.data.autofill.entities.ParsedStructure
import com.enekocm.securevault.data.autofill.utils.Utils
import com.enekocm.securevault.domain.repository.PasswordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class FillRequestHandler(private val context: Context) {

    fun handleFillRequest(
        request: FillRequest,
        callback: FillCallback,
        passwordRepository: PasswordRepository?
    ) {
        val structure = request.fillContexts.lastOrNull()?.structure ?: return
        val parsedStructure = StructureParser.parseStructure(structure) ?: return

        CoroutineScope(Dispatchers.Main).launch {
            if (!Utils.isAppKeyAvailable()) {
                handleUnauthenticatedFill(request, structure, parsedStructure, callback)
                return@launch
            }

            handleAuthenticatedFill(request, structure, parsedStructure, callback, passwordRepository)
        }
    }

    private fun handleUnauthenticatedFill(
        request: FillRequest,
        structure: AssistStructure,
        parsedStructure: ParsedStructure,
        callback: FillCallback
    ) {
        val loginInlinePresentation = Utils.createInlinePresentation(
            context,
            request.inlineSuggestionsRequest,
            "Login to Secure Vault"
        )
        val loginIntent = Utils.createLoginIntent(
            context,
            structure.activityComponent?.packageName ?: "",
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

    private fun handleAuthenticatedFill(
        request: FillRequest,
        structure: AssistStructure,
        parsedStructure: ParsedStructure,
        callback: FillCallback,
        passwordRepository: PasswordRepository?
    ) {
        if (passwordRepository == null) {
            callback.onSuccess(null)
            return
        }

        val (username, password) = Fetch.fetchPassword(
            structure.activityComponent.packageName,
            passwordRepository.getAllPasswords()
        )

        val inlinePresentation = Utils.createInlinePresentation(
            context,
            request.inlineSuggestionsRequest,
            username ?: return
        ) ?: run {
            callback.onSuccess(null)
            return
        }

        if (password != null) {
            val dataset = Dataset.Builder()
                .setValue(
                    parsedStructure.usernameId,
                    AutofillValue.forText(username)
                )
                .setValue(
                    parsedStructure.passwordId,
                    AutofillValue.forText(password)
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
