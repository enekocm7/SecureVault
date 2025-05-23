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
import com.example.securevault.databinding.DialogImportBinding
import com.example.securevault.ui.viewmodel.dialogs.ImportPasswordViewModel
import kotlinx.coroutines.launch

class ImportPasswordDialog : DialogFragment() {

    companion object {
        private const val ENCRYPTED = "application/sv"
        private const val CSV = "text/csv"
    }

    private lateinit var binding: DialogImportBinding
    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    fileUri = it
                    val fileName = getFileNameFromUri(it)
                    binding.filePathInput.setText(fileName ?: it.lastPathSegment)
                }
            }
        }

    private var fileUri: Uri? = null
    private val viewModel: ImportPasswordViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogImportBinding.inflate(layoutInflater)

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
        setListeners()
        observeLoadingState()
        return dialog
    }

    private fun setPasswordInput() {
        binding.importMethodGroup.setOnCheckedChangeListener { _, _ ->
            binding.passwordSection.visibility =
                if (binding.radioEncrypted.isChecked) View.VISIBLE else View.GONE

        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        if (uri.scheme != "content") {
            return uri.path?.substringAfterLast('/')
        }

        return context?.contentResolver?.query(
            uri, null, null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex =
                    cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                if (nameIndex != -1) cursor.getString(nameIndex) else null
            } else null
        }
    }

    private fun setListeners() {
        binding.selectFileButton.setOnClickListener {
            val fileType = if (binding.radioEncrypted.isChecked) ENCRYPTED else CSV
            openFile(fileType)
        }

        binding.btnImport.setOnClickListener {
            val isEncrypted = binding.radioEncrypted.isChecked
            val password = binding.passwordInput.text.toString()

            if (isEncrypted && password.isBlank()) {
                showMissingPasswordToast()
                return@setOnClickListener
            }
            if (fileUri == null) showMissingFileToast() else handleFile(fileUri!!)
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun showMissingPasswordToast() {
        Toast.makeText(
            context,
            getString(R.string.decryption_password_toast),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun openFile(fileType: String) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = fileType
        }
        filePickerLauncher.launch(intent)
    }

    private fun handleFile(uri: Uri) {
        if (binding.radioEncrypted.isChecked) {
            context?.contentResolver?.openInputStream(uri)?.bufferedReader().use {
                val passwords = it?.readText() ?: ""
                try {
                    viewModel.insertAllPasswords(passwords, binding.passwordInput.text.toString())
                } catch (_: Exception) {
                    showWrongPasswordToast()
                }
            }
        } else if (binding.radioCsv.isChecked) {
            viewModel.insertAllPasswords(uri)
        }
    }

    private fun showSuccessToast() {
        Toast.makeText(context, getString(R.string.csv_import_toast), Toast.LENGTH_LONG)
            .show()
    }

    private fun observeLoadingState() {
        lifecycleScope.launch {
            viewModel.loading.collect { isLoading ->
                if (!isLoading && binding.progressContainer.isVisible) {
                    showSuccessToast()
                    dismiss()
                }

                binding.progressContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.buttonsContainer.isEnabled = !isLoading
                binding.btnImport.isEnabled = !isLoading
                binding.btnCancel.isEnabled = !isLoading
            }
        }
    }

    private fun showMissingFileToast() {
        Toast.makeText(
            context,
            getString(R.string.missing_file_toast),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showWrongPasswordToast() {
        Toast.makeText(
            context,
            getString(R.string.wrong_password_toast),
            Toast.LENGTH_LONG
        ).show()
    }
}

