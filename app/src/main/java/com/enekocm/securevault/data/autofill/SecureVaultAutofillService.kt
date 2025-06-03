package com.enekocm.securevault.data.autofill

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.assist.AssistStructure
import android.content.Intent
import android.graphics.drawable.Icon
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

                        .setValue(
                            parsedStructure.usernameId,
                            AutofillValue.forText(""),
                        )
                        /*

How to do this correctly:

    In your AutofillService — create a Dataset with:

        empty or placeholder values for username/password

        inline presentation (the UI snippet shown to user)

        set the authentication intent pointing to your login activity's PendingIntent

val loginIntent = createLoginPendingIntent()

val dataset = Dataset.Builder()
    .setInlinePresentation(loginInlinePresentation)
    .setValue(parsedStructure.usernameId, AutofillValue.forText("")) // empty for now
    .setValue(parsedStructure.passwordId, AutofillValue.forText("")) // empty for now
    .setAuthentication(loginIntent.intentSender) // fires login activity when tapped
    .build()

callback.onSuccess(FillResponse.Builder().addDataset(dataset).build())

    In your LoginActivity — after user logs in:

    Build a Dataset with the real username/password values

    Pack that into an Intent extra with key AutofillManager.EXTRA_AUTHENTICATION_RESULT

    Set that Intent as the activity result

    Call finish() to close the login activity and send the data back to the autofill framework

Example:

import android.view.autofill.AutofillManager

// after successful login
val username = "realUser"
val password = "realPass"

val resultDataset = Dataset.Builder()
    .setValue(parsedStructure.usernameId, AutofillValue.forText(username))
    .setValue(parsedStructure.passwordId, AutofillValue.forText(password))
    .build()

val resultIntent = Intent().apply {
    putExtra(
        AutofillManager.EXTRA_AUTHENTICATION_RESULT,
        resultDataset
    )
}

setResult(Activity.RESULT_OK, resultIntent)
finish()

What happens next?

    Android receives your Dataset from LoginActivity

    Autofill UI automatically fills the username and password fields in the original app (Netflix, etc.)

Important:

    parsedStructure.usernameId and passwordId need to be accessible or passed to your login activity so you can set them correctly in the result Dataset.

    Alternatively, you can pass the IDs in the intent extras when launching LoginActivity.

    The autofill framework requires the exact field IDs used by the app for correct filling.

Summary:
AutofillService Dataset	Has empty values + .setAuthentication() pointing to login activity
LoginActivity result Dataset	Has real username/password values returned via setResult()
Android framework	Autofills the original app with the real credentials

If you want, I can help with:

    Passing the autofill field IDs safely to your login activity

    Sample code for LoginActivity receiving those IDs and returning the result

Would you like me to?
                         */

                        .setValue(
                            parsedStructure.passwordId,
                            AutofillValue.forText(""),
                        )
                        .setAuthentication(createLoginIntent().intentSender)
                        .build()

                    val response = FillResponse.Builder()
                        .addDataset(dataset)
                        .build()
                    callback.onSuccess(
                        response
                    )
                } else {
                    callback.onSuccess(null)
                }
                return@launch
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
        text: String,
        pendingIntent: PendingIntent = createLoginIntent()
    ): InlinePresentation? {
        request ?: return null

        if (request.maxSuggestionCount <= 0) return null

        val spec = request.inlinePresentationSpecs.firstOrNull() ?: return null


        return InlinePresentation(
            InlineSuggestionUi.newContentBuilder(pendingIntent).apply {
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

    private fun createLoginIntent(): PendingIntent {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

}
