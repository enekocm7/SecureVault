package ui.view.dialogs

import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.enekocm.securevault.R
import dagger.hilt.android.AndroidEntryPoint
import domain.model.ImportState
import ui.compose.dialogs.ImportDialogContent
import ui.compose.theme.SecureVaultTheme
import ui.viewmodel.dialogs.ImportPasswordViewModel
import utils.FilePicker
import utils.FilePickerType
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImportPasswordDialog : DialogFragment() {

    companion object {
        private const val ENCRYPTED = "application/octet-stream"
        private const val CSV = "text/*"
    }

    private val viewModel: ImportPasswordViewModel by viewModels()
    private var fileUri: Uri? = null
    private var fileNameState: ((String) -> Unit)? = null
    private var encryptedState: (() -> Boolean)? = null

    private val filePicker = FilePicker(fragment = this) { uri ->
        val fileName: String = getFileNameFromUri(uri) ?: uri.lastPathSegment ?: return@FilePicker
        val isEncrypted = encryptedState?.invoke() ?: true
        val isCsv = !(encryptedState?.invoke() ?: true)
        when {
            isEncrypted && !fileName.endsWith(".sv") -> showToast(getString(R.string.file_type_sv_error))
            isCsv && !fileName.endsWith(".csv") -> showToast(getString(R.string.file_type_csv_error))
            else -> {
                fileUri = uri
                fileNameState?.invoke(fileName)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        lifecycleScope.launch {
            viewModel.importState.collect { state ->
                when (state) {
                    ImportState.WrongPassword -> showToast(getString(R.string.wrong_password_toast))
                    ImportState.CsvFormatError -> showToast(getString(R.string.csv_file_creation_error))
                    ImportState.Success -> {
                        showToast(getString(R.string.import_toast))
                        dismiss()
                    }
                    null -> Unit
                }
                if (state != null) viewModel.clearError()
            }
        }

        dialog.setContentView(
            ComposeView(requireContext()).apply {
                setContent {
                    SecureVaultTheme {
                        val loading by viewModel.loading.collectAsState()
                        var path by remember { mutableStateOf("") }
                        var enc by remember { mutableStateOf(true) }
                        fileNameState = { path = it }
                        encryptedState = { enc }
                        ImportDialogContent(
                            onBrowse = {
                                filePicker.launch(FilePickerType.FILE, if (enc) ENCRYPTED else CSV)
                            },
                            onImport = { _, password, encrypted ->
                                enc = encrypted
                                val uri = fileUri
                                if (uri == null) {
                                    showToast(getString(R.string.missing_file_toast))
                                    return@ImportDialogContent
                                }
                                if (encrypted) {
                                    if (password.isBlank()) {
                                        showToast(getString(R.string.decryption_password_toast))
                                        return@ImportDialogContent
                                    }
                                    context?.contentResolver?.openInputStream(uri)?.bufferedReader().use {
                                        val text = it?.readText() ?: ""
                                        try {
                                            viewModel.insertAllPasswords(text, password)
                                        } catch (_: Exception) {
                                            showToast(getString(R.string.wrong_password_toast))
                                        }
                                    }
                                } else {
                                    viewModel.insertAllPasswords(uri)
                                }
                            },
                            onCancel = { dismiss() },
                            filePath = path,
                            isBusy = loading
                        )
                    }
                }
            }
        )
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return dialog
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        if (uri.scheme != "content") return uri.path?.substringAfterLast('/')
        return context?.contentResolver?.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                if (idx != -1) cursor.getString(idx) else null
            } else null
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
