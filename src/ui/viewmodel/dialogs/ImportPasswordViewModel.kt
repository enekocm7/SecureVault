package ui.viewmodel.dialogs

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import di.DispatcherProvider
import domain.model.ImportState
import domain.model.PasswordDto
import domain.usecases.csv.ReadCsv
import domain.usecases.password.GetPasswordsFromEncrypted
import domain.usecases.password.InsertAllPasswords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImportPasswordViewModel @Inject constructor(
    private val getPasswordsFromEncrypted: GetPasswordsFromEncrypted,
    private val readCsv: ReadCsv,
    private val saveAllPasswords: InsertAllPasswords,
    private val dispatcherProvider: DispatcherProvider
) :
    ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _importState = MutableStateFlow<ImportState?>(null)
    val importState = _importState.asStateFlow()

    private fun insertAllPasswords(passwords: List<PasswordDto>) {
        _loading.value = true
        saveAllPasswords(passwords)
        _loading.value = false
    }

    fun insertAllPasswords(encryptedPasswords: String, key: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            _loading.value = true
            try {
                insertAllPasswords(getPasswordsFromEncrypted(encryptedPasswords, key))
                _importState.value = ImportState.Success
            } catch (_: Exception) {
                _importState.value = ImportState.WrongPassword
            }
            _loading.value = false
        }
    }

    fun insertAllPasswords(uri: Uri) {
        viewModelScope.launch(dispatcherProvider.io) {
            _loading.value = true
            try {
                insertAllPasswords(readCsv(uri))
                _importState.value = ImportState.Success
            } catch (_: Exception) {
                _importState.value = ImportState.CsvFormatError
            }
            _loading.value = false
        }
    }

    fun clearError() {
        _importState.value = null
    }

}
