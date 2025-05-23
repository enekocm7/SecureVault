package com.example.securevault.ui.viewmodel.dialogs

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.securevault.domain.usecases.csv.WriteCsv
import com.example.securevault.domain.usecases.password.GetAllPasswords
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ExportPasswordViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val writeCsv: WriteCsv,
    private val getAllPasswords: GetAllPasswords
) : ViewModel() {

    private val fileNameCsv = "passwords.csv"
    private val fileNameSv = "passwords.sv"
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    fun createCsv(folderUri: Uri) {
        _loading.value = true
        val fileUri = createFileInFolder(folderUri, fileNameCsv, "text/csv")
        fileUri?.let {
            writeCsv(it, getAllPasswords())
        }
        _loading.value = false
    }
    fun createSv(folderUri: Uri, password: String) {
        TODO()
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