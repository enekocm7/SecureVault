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
import com.enekocm.securevault.data.repository.PasswordRepositoryImpl
import com.enekocm.securevault.domain.repository.PasswordRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SecureVaultAutofillService : AutofillService() {

    private var passwordRepository: PasswordRepository? = null
    private lateinit var fillRequestHandler: FillRequestHandler
    private lateinit var saveRequestHandler: SaveRequestHandler

    @Inject
    lateinit var storage: PasswordStorage

    @Inject
    lateinit var encryptor: FileEncryptor

    override fun onCreate() {
        super.onCreate()
        fillRequestHandler = FillRequestHandler(baseContext)
        saveRequestHandler = SaveRequestHandler()
    }

    @Suppress("DEPRECATION")
    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        if (passwordRepository == null) {
            passwordRepository = PasswordRepositoryImpl(storage, encryptor)
        }

        fillRequestHandler.handleFillRequest(
            request,
            callback,
            passwordRepository
        )
    }

    override fun onSaveRequest(
        request: SaveRequest,
        callback: SaveCallback
    ) {
        if (passwordRepository == null) {
            passwordRepository = PasswordRepositoryImpl(storage, encryptor)
        }

        saveRequestHandler.handleSaveRequest(
            request,
            callback,
            passwordRepository
        )
    }
}