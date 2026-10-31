package ui.view.dialogs

import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
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
import com.enekocm.securevault.R
import ui.compose.dialogs.ExportDialogContent
import ui.compose.theme.SecureVaultTheme
import ui.viewmodel.dialogs.ExportPasswordViewModel
import ui.viewmodel.exportPasswordFactory
import utils.FilePicker
import utils.FilePickerType

class ExportPasswordDialog : DialogFragment() {

    private val viewModel: ExportPasswordViewModel by viewModels { exportPasswordFactory(requireActivity().application) }
    private var folderUri: Uri? = null
    private var pathSetter: ((String) -> Unit)? = null

    private val filePicker = FilePicker(fragment = this) { uri ->
        folderUri = uri
        val name = uri.lastPathSegment?.split(':')?.lastOrNull() ?: uri.lastPathSegment
        pathSetter?.invoke(name ?: "")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.setContentView(
            ComposeView(requireContext()).apply {
                setContent {
                    SecureVaultTheme {
                        val loading by viewModel.loading.collectAsState()
                        var path by remember { mutableStateOf("") }
                        pathSetter = { path = it }
                        ExportDialogContent(
                            onBrowse = { filePicker.launch(FilePickerType.FOLDER) },
                            onExport = { _, password, confirm, encrypted ->
                                val folder = folderUri
                                if (folder == null) {
                                    Toast.makeText(context, getString(R.string.missing_folder_toast), Toast.LENGTH_LONG).show()
                                    return@ExportDialogContent
                                }
                                if (!encrypted) {
                                    viewModel.createCsv(folder)
                                } else {
                                    if (password.isBlank()) {
                                        Toast.makeText(context, getString(R.string.missing_password_toast), Toast.LENGTH_LONG).show()
                                        return@ExportDialogContent
                                    }
                                    if (password != confirm) {
                                        Toast.makeText(context, getString(R.string.different_password_toast), Toast.LENGTH_LONG).show()
                                        return@ExportDialogContent
                                    }
                                    viewModel.createSv(folder, password)
                                }
                            },
                            onCancel = { dismiss() },
                            path = path,
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
}
