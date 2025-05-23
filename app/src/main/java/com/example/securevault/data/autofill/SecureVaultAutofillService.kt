package com.example.securevault.data.autofill

import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.example.securevault.data.crypto.AppKeyProvider
import com.example.securevault.data.json.crypto.FileEncryptor
import com.example.securevault.data.json.storage.PasswordStorage
import com.example.securevault.data.repository.PasswordRepositoryImpl
import com.example.securevault.domain.repository.PasswordRepository
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

	override fun onFillRequest(
		request: FillRequest,
		cancellationSignal: CancellationSignal,
		callback: FillCallback
	) {
		CoroutineScope(Dispatchers.IO).launch {
			val appKey: ByteArray? = try {
				AppKeyProvider.getAppKey()
			}catch (_: Exception){
				null
			}

			if (appKey == null) {
				callback.onSuccess(null)
				return@launch
			}

			if (passwordRepository == null) {
				passwordRepository = PasswordRepositoryImpl(storage,encryptor)
			}

			val structure = request.fillContexts.lastOrNull()?.structure
			if (structure == null) {
				callback.onSuccess(null)
				return@launch
			}

			val parsedStructure = StructureParser.parseStructure(structure)
			val (username, password) = Fetch.fetchPassword(
				structure.activityComponent.packageName,
				passwordRepository!!
			) ?: run {
				callback.onSuccess(null)
				return@launch
			}

			val usernamePresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1).apply {
				setTextViewText(android.R.id.text1, username)
			}
			val passwordPresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1).apply {
				setTextViewText(android.R.id.text1, password)
			}

			val fillResponse = FillResponse.Builder()
				.addDataset(
					Dataset.Builder()
						.setValue(parsedStructure.usernameId, AutofillValue.forText(username), usernamePresentation)
						.setValue(parsedStructure.passwordId, AutofillValue.forText(password), passwordPresentation)
						.build()
				).build()

			callback.onSuccess(fillResponse)
		}
	}

	override fun onSaveRequest(
		request: SaveRequest,
		callback: SaveCallback
	) {
		TODO("Not yet implemented")
	}

}