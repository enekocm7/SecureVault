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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.securevault.R
import com.example.securevault.databinding.DialogImportBinding
import com.example.securevault.domain.model.ImportState
import com.example.securevault.ui.viewmodel.dialogs.ImportPasswordViewModel
import kotlinx.coroutines.launch

class ImportPasswordDialog : DialogFragment() {

    companion object {
        private const val ENCRYPTED = "*/*"
        private const val CSV = "text/*"
    }

    private lateinit var binding: DialogImportBinding
    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    val fileName = getFileNameFromUri(it) ?: it.lastPathSegment ?: ""
                    val isEncrypted = binding.radioEncrypted.isChecked
                    val isCsv = binding.radioCsv.isChecked

                    when {
                        isEncrypted && !fileName.endsWith(".sv") -> {
                            showToast(getString(R.string.file_type_sv_error))
                        }

                        isCsv && !fileName.endsWith(".csv") -> {
                            showToast(getString(R.string.file_type_csv_error))
                        }

                        else -> {
                            fileUri = it
                            binding.filePathInput.setText(fileName)
                        }
                    }
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
        observeErrorState()
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
                showToast(getString(R.string.decryption_password_toast))
                return@setOnClickListener
            }
            if (fileUri == null) showToast(getString(R.string.missing_file_toast)) else handleFile(
                fileUri!!
            )
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
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
                    showToast(getString(R.string.wrong_password_toast))
                }
            }
        } else if (binding.radioCsv.isChecked) {
            viewModel.insertAllPasswords(uri)
        }
    }

    private fun observeLoadingState() {
        lifecycleScope.launch {
            viewModel.loading.collect { isLoading ->
                binding.progressContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.buttonsContainer.isEnabled = !isLoading
                binding.btnImport.isEnabled = !isLoading
                binding.btnCancel.isEnabled = !isLoading
            }
        }
    }

    private fun observeErrorState() {
        lifecycleScope.launch {
            viewModel.importState.collect { state ->
                when (state) {
                    ImportState.WrongPassword ->
                        showToast(getString(R.string.wrong_password_toast))

                    ImportState.CsvFormatError ->
                        showToast(getString(R.string.csv_file_creation_error))

                    ImportState.Success -> {
                        showToast(getString(R.string.import_toast))
                        dismiss()
                    }


                    null -> {
                        /*Nothing*/
                    }
                }

                if (state != null) {
                    viewModel.clearError()
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}

