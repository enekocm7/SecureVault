package com.example.securevault.data.autofill

import android.app.PendingIntent
import android.app.assist.AssistStructure
import android.content.Intent
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveInfo
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.example.securevault.data.autofill.StructureParser.traverseViewNode
import com.example.securevault.data.crypto.AppKeyProvider
import com.example.securevault.data.json.crypto.FileEncryptor
import com.example.securevault.data.json.storage.PasswordStorage
import com.example.securevault.data.repository.PasswordRepositoryImpl
import com.example.securevault.domain.repository.PasswordRepository
import com.example.securevault.ui.view.SavePasswordActivity
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
			if (!isAppKeyAvailable()) {
				return@launch
			}

			passwordRepository = PasswordRepositoryImpl(storage, encryptor)

			val structure = request.fillContexts.lastOrNull()?.structure
			if (structure == null) {
				return@launch
			}

			val parsedStructure = StructureParser.parseStructure(structure) ?: run {
				return@launch
			}
			val (username, password) = Fetch.fetchPassword(structure.activityComponent.packageName,passwordRepository!!)

			val usernamePresentation =
				RemoteViews(packageName, android.R.layout.simple_list_item_1).apply {
					setTextViewText(android.R.id.text1, username)
				}
			val passwordPresentation =
				RemoteViews(packageName, android.R.layout.simple_list_item_1).apply {
					setTextViewText(android.R.id.text1, password)
				}


			val fillResponse = FillResponse.Builder()
				.addDataset(
					Dataset.Builder()
						.setValue(
							parsedStructure.usernameId,
							AutofillValue.forText(username),
							usernamePresentation
						)
						.setValue(
							parsedStructure.passwordId,
							AutofillValue.forText(password),
							passwordPresentation
						)
						.build()
				)
				.setSaveInfo(
					SaveInfo.Builder(
						SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD,
						arrayOf(parsedStructure.usernameId,parsedStructure.passwordId)
					).build()
				)
				.build()
			callback.onSuccess(fillResponse)
		}
	}

	override fun onSaveRequest(
		request: SaveRequest,
		callback: SaveCallback
	) {
		val structure = request.fillContexts.lastOrNull()?.structure
		if (structure == null) {
			return
		}

		val packageName = structure.activityComponent.packageName
		val credentials = extractCredentialsFromStructure(structure)

		if (credentials.isValid()) {
			try {
				val intent = Intent(this, SavePasswordActivity::class.java).apply {
					flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
					putExtra("url", packageName)
					putExtra("username", credentials.username)
					putExtra("password", credentials.password)
				}

				val pendingIntent = PendingIntent.getActivity(
					this,
					SavePasswordActivity.REQUEST_SAVE_PASSWORD,
					intent,
					PendingIntent.FLAG_IMMUTABLE
				).intentSender

				callback.onSuccess(pendingIntent)
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

			if (username == null && (hint.contains("user") || hint.contains("email") || idEntry.contains(
					"user"
				))
			) {
				username = node.text?.toString()
			}

			if (password == null && (hint.contains("pass") || idEntry.contains("pass"))) {
				password = node.text?.toString()
			}
		}

		return Credentials(username, password)
	}

}


