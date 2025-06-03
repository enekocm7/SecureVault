package com.enekocm.securevault.data.autofill

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.assist.AssistStructure
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.InlinePresentation
import android.service.autofill.SaveCallback
import android.service.autofill.SaveInfo
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillValue
import android.view.inputmethod.InlineSuggestionsRequest
import androidx.annotation.RequiresApi
import androidx.autofill.inline.v1.InlineSuggestionUi
import com.enekocm.securevault.R
import com.enekocm.securevault.data.autofill.StructureParser.traverseViewNode
import com.enekocm.securevault.data.crypto.AppKeyProvider
import com.enekocm.securevault.data.json.crypto.FileEncryptor
import com.enekocm.securevault.data.json.model.Password
import com.enekocm.securevault.data.json.storage.PasswordStorage
import com.enekocm.securevault.data.repository.PasswordRepositoryImpl
import com.enekocm.securevault.domain.repository.PasswordRepository
import com.enekocm.securevault.ui.view.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SecureVaultAutofillService : AutofillService() {

    private var passwordRepository: PasswordRepository? = null

    @Inject
    lateinit var storage: PasswordStorage

    @Inject
    lateinit var encryptor: FileEncryptor

    @RequiresApi(Build.VERSION_CODES.S)
    @Suppress("DEPRECATION")
    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val structure = request.fillContexts.lastOrNull()?.structure
            val parsedStructure = if (structure != null) {
                StructureParser.parseStructure(structure)
            } else null

            if (!isAppKeyAvailable()) {
                val loginInlinePresentation = createInlinePresentation(
                    request.inlineSuggestionsRequest,
                    "Login to Secure Vault"
                )

                if (loginInlinePresentation != null && parsedStructure != null) {
                    val dataset = Dataset.Builder()
                        .setInlinePresentation(loginInlinePresentation)

                    dataset.setValue(
                        parsedStructure.usernameId,
                        AutofillValue.forText(""),
                    )

                    dataset.setValue(
                        parsedStructure.passwordId,
                        AutofillValue.forText(""),
                    )

                    callback.onSuccess(
                        FillResponse.Builder()
                            .addDataset(dataset.build())
                            .build()
                    )
                } else {
                    callback.onSuccess(null)
                }
            }

            passwordRepository = PasswordRepositoryImpl(storage, encryptor)

            if (structure == null || parsedStructure == null) {
                callback.onSuccess(null)
                return@launch
            }

            val (username, password) = Fetch.fetchPassword(
                structure.activityComponent.packageName,
                passwordRepository!!.getAllPasswords()
            )

            val inlinePresentation = createInlinePresentation(
                request.inlineSuggestionsRequest,
                username ?: "No saved credentials"
            ) ?: run {
                callback.onSuccess(null)
                return@launch
            }

            if (username != null && password != null) {
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
                        SaveInfo.Builder(
                            SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                            arrayOf(parsedStructure.usernameId, parsedStructure.passwordId)
                        ).build()
                    )
                    .build()

                callback.onSuccess(fillResponse)
            } else {
                callback.onSuccess(
                    FillResponse.Builder()
                        .setSaveInfo(
                            SaveInfo.Builder(
                                SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                                arrayOf(parsedStructure.usernameId, parsedStructure.passwordId)
                            ).build()
                        )
                        .build()
                )
            }
        }
    }

    override fun onSaveRequest(
        request: SaveRequest,
        callback: SaveCallback
    ) {
        val structure = request.fillContexts.lastOrNull()?.structure ?: run {
            return
        }

        val packageName = structure.activityComponent.packageName
        val credentials = extractCredentialsFromStructure(structure)

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
                passwordRepository = PasswordRepositoryImpl(storage, encryptor)
                passwordRepository?.insertPassword(password)
                callback.onSuccess()
            } catch (e: Exception) {
                callback.onFailure("Failed to show save password dialog: ${e.message}")
            }
        } else {
            return
        }
    }

    private suspend fun isAppKeyAvailable(): Boolean {
        return try {
            AppKeyProvider.getAppKey()
            true
        } catch (_: Exception) {
            false
        }
    }

    private data class Credentials(val username: String?, val password: String?) {
        fun isValid() = !username.isNullOrBlank() && !password.isNullOrBlank()
    }

    private fun extractCredentialsFromStructure(structure: AssistStructure): Credentials {
        var username: String? = null
        var password: String? = null

        traverseViewNode(structure.getWindowNodeAt(0).rootViewNode) { node ->
            val hint = node.hint?.lowercase() ?: ""
            val idEntry = node.idEntry?.lowercase() ?: ""

            if (username == null && (hint.contains("user") ||
                        hint.contains("email") || idEntry.contains("user"))
            ) {
                username = node.text?.toString()
            }
            if (password == null && (hint.contains("pass") || idEntry.contains("pass"))) {
                password = node.text?.toString()
            }
        }

        return Credentials(username, password)
    }

    @SuppressLint("RestrictedApi")
    private fun createInlinePresentation(
        request: InlineSuggestionsRequest?,
        text: String
    ): InlinePresentation? {
        request ?: return null

        if (request.maxSuggestionCount <= 0) return null

        val spec = request.inlinePresentationSpecs.firstOrNull() ?: return null

        val loginIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return InlinePresentation(
            InlineSuggestionUi.newContentBuilder(loginIntent).apply {
                setTitle(text)
                setStartIcon(
                    Icon.createWithResource(
                        this@SecureVaultAutofillService,
                        R.mipmap.ic_launcher_round
                    )
                )
            }.build().slice,
            spec,
            false
        )
    }

}
