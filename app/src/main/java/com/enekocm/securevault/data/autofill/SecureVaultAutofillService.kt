package com.enekocm.securevault.data.autofill

import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import com.enekocm.securevault.data.autofill.handlers.FillRequestHandler
import com.enekocm.securevault.data.autofill.handlers.SaveRequestHandler
import com.enekocm.securevault.data.json.crypto.FileEncryptor
import com.enekocm.securevault.data.json.storage.PasswordStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SecureVaultAutofillService : AutofillService() {

    @Inject
    lateinit var fillRequestHandler: FillRequestHandler

    @Inject
    lateinit var saveRequestHandler: SaveRequestHandler

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
        fillRequestHandler.handleFillRequest(
            request,
            callback,
            storage,
            encryptor
        )
    }

    override fun onSaveRequest(
        request: SaveRequest,
        callback: SaveCallback
    ) {
        saveRequestHandler.handleSaveRequest(
            request,
            callback,
            storage,
            encryptor
        )
    }
}