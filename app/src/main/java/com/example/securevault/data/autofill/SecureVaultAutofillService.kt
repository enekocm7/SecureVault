package com.example.securevault.data.autofill

import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest

class SecureVaultAutofillService: AutofillService() {
    override fun onFillRequest(
        fillRequest: FillRequest,
        cancellationSignal: CancellationSignal,
        fillCallback: FillCallback
    ) {
        TODO("Not yet implemented")
    }

    override fun onSaveRequest(
        saveRequest: SaveRequest,
        saveCallback: SaveCallback
    ) {
        TODO("Not yet implemented")
    }
}