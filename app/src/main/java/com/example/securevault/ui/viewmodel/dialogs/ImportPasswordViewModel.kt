package com.example.securevault.ui.viewmodel.dialogs

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.securevault.domain.model.PasswordDto
import com.example.securevault.domain.usecases.csv.ReadCsv
import com.example.securevault.domain.usecases.password.GetPasswordsFromEncrypted
import com.example.securevault.domain.usecases.password.InsertPasswords
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImportPasswordViewModel @Inject constructor(
    private val insertPasswords: InsertPasswords,
    private val getPasswordsFromEncrypted: GetPasswordsFromEncrypted,
    private val readCsv: ReadCsv
) :
    ViewModel() {


    private fun insertAllPasswords(passwords: List<PasswordDto>) {
        insertPasswords(passwords)
    }

    fun insertAllPasswords(encryptedPasswords: String, key: String) {
        insertAllPasswords(getPasswordsFromEncrypted(encryptedPasswords, key))
    }

    fun insertAllPasswords(uri: Uri) {
        insertAllPasswords(readCsv(uri))
    }

}