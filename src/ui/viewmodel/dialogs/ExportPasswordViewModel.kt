package ui.viewmodel.dialogs

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import di.DispatcherProvider
import domain.usecases.csv.WriteCsv
import domain.usecases.password.GetAllPasswords
import domain.usecases.sv.WriteSv
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExportPasswordViewModel @Inject constructor(
    private val context: Context,
    private val writeCsv: WriteCsv,
    private val writeSv: WriteSv,
    private val getAllPasswords: GetAllPasswords,
    private val dispatchers : DispatcherProvider
) : ViewModel() {

    private val fileNameCsv = "passwords.csv"
    private val fileNameSv = "passwords.sv"
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    fun createCsv(folderUri: Uri) {
        viewModelScope.launch(dispatchers.io){
            _loading.value = true
            val fileUri = createFileInFolder(folderUri, fileNameCsv, "text/csv")
            fileUri?.let {
                writeCsv(it, getAllPasswords())
            }
            _loading.value = false
        }
    }

    fun createSv(folderUri: Uri, password: String) {
        viewModelScope.launch(dispatchers.io){
            _loading.value = true
            val fileUri = createFileInFolder(folderUri, fileNameSv, "text/sv")
            fileUri?.let {
                writeSv(it, getAllPasswords(), password)
            }
            _loading.value = false
        }
    }

    private fun createFileInFolder(folderUri: Uri, fileName: String, mimeType: String): Uri? {
        return try {
            val docUri = DocumentsContract.buildDocumentUriUsingTree(
                folderUri,
                DocumentsContract.getTreeDocumentId(folderUri)
            )
            DocumentsContract.createDocument(
                context.contentResolver,
                docUri,
                mimeType,
                fileName
            )
        } catch (e: Exception) {
            Log.d("Exception", e.message ?: "")
            null
        }
    }

}