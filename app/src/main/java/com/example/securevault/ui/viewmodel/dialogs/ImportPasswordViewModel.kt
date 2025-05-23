package com.example.securevault.ui.viewmodel.dialogs

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securevault.di.DispatcherProvider
import com.example.securevault.domain.model.PasswordDto
import com.example.securevault.domain.usecases.csv.ReadCsv
import com.example.securevault.domain.usecases.password.GetPasswordsFromEncrypted
import com.example.securevault.domain.usecases.password.InsertAllPasswords
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportPasswordViewModel @Inject constructor(
    private val getPasswordsFromEncrypted: GetPasswordsFromEncrypted,
    private val readCsv: ReadCsv,
    private val saveAllPasswords: InsertAllPasswords,
    private val dispatcherProvider: DispatcherProvider
) :
    ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private fun insertAllPasswords(passwords: List<PasswordDto>) {
        _loading.value = true
        saveAllPasswords(passwords)
        _loading.value = false
    }

    fun insertAllPasswords(encryptedPasswords: String, key: String) {
        _loading.value = true
        insertAllPasswords(getPasswordsFromEncrypted(encryptedPasswords, key))
        _loading.value = false
    }

    fun insertAllPasswords(uri: Uri) {
        viewModelScope.launch(dispatcherProvider.io) {
            _loading.value = true
            val list = readCsv(uri)
            insertAllPasswords(list)
            _loading.value = false
        }
    }

}
