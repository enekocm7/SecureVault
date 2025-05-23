package com.example.securevault.ui.view.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.securevault.R
import com.example.securevault.databinding.DialogExportBinding
import com.example.securevault.ui.viewmodel.dialogs.ExportPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExportPasswordDialog : DialogFragment() {

    private lateinit var binding: DialogExportBinding
    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    val flags = result.data?.flags ?: 0
                    val takeFlags =
                        flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    context?.contentResolver?.takePersistableUriPermission(it, takeFlags)

                    folderUri = it
                    val fileName = getFolderNameFromUri(it)
                    binding.pathInput.setText(fileName ?: it.lastPathSegment)
                }
            }
        }

    private var folderUri: Uri? = null
    private val viewModel: ExportPasswordViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogExportBinding.inflate(layoutInflater)

        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.9).toInt()

        val dialog = super.onCreateDialog(savedInstanceState).apply {
            setContentView(binding.root)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            window?.setLayout(
                width,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }

        setPasswordInput()
        observeLoadingState()
        setListeners()
        return dialog
    }

    private fun setListeners() {
        binding.browseButton.setOnClickListener {
            selectFolder()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnExport.setOnClickListener {
            val uri = folderUri
            if (uri == null) showToast(getString(R.string.missing_folder_toast)) else handleFile(uri)
        }
    }

    private fun observeLoadingState() {
        lifecycleScope.launch {
            viewModel.loading.collect { isLoading ->
                if (!isLoading && binding.progressContainer.isVisible) {
                    showToast(getString(R.string.exported_passwords_successfully_toast))
                    dismiss()
                }

                binding.progressContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.buttonsContainer.isEnabled = !isLoading
                binding.btnExport.isEnabled = !isLoading
                binding.btnCancel.isEnabled = !isLoading
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun handleFile(folderUri: Uri) {
        if (binding.radioCsv.isChecked) {
            viewModel.createCsv( folderUri)
        } else {
            val password = getPassword()
            if (binding.radioEncrypted.isChecked && password != null) {
                viewModel.createSv( folderUri, password)
            }
        }
    }



    private fun selectFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        filePickerLauncher.launch(intent)
    }

    private fun getFolderNameFromUri(uri: Uri): String? {
        context?.contentResolver?.query(
            DocumentsContract.buildDocumentUriUsingTree(
                uri,
                DocumentsContract.getTreeDocumentId(uri)
            ),
            arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME),
            null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex =
                    cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                if (nameIndex != -1) {
                    return cursor.getString(nameIndex)
                }
            }
        }

        return uri.lastPathSegment?.split(':')?.lastOrNull() ?: uri.lastPathSegment
    }

    private fun setPasswordInput() {
        binding.exportMethodGroup.setOnCheckedChangeListener { _, _ ->
            if (binding.radioEncrypted.isChecked) {
                binding.passwordSection.visibility = View.VISIBLE
            } else {
                binding.passwordSection.visibility = View.GONE
            }
        }
    }

    private fun getPassword(): String? {
        val password = binding.passwordInput.text
        val confirmPassword = binding.confirmPasswordInput.text
        return if (password.isNullOrBlank()) {
            showToast(getString(R.string.missing_password_toast))
            null
        } else if (password.toString() != confirmPassword.toString()) {
            showToast(getString(R.string.different_password_toast))
            null
        } else {
            password.toString()
        }
    }


}

