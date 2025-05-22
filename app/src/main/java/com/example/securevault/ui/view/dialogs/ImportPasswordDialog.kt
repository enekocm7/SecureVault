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
import com.example.securevault.R
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.securevault.databinding.DialogImportFileBinding
import com.example.securevault.ui.viewmodel.dialogs.ImportPasswordViewModel
import java.io.File

class ImportPasswordDialog : DialogFragment() {

    companion object {
        private const val ENCRYPTED = "application/sv"
        private const val CSV = "text/csv"
    }

    private lateinit var binding: DialogImportFileBinding
    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>

    private var file: File? = null
    private val viewModel: ImportPasswordViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogImportFileBinding.inflate(layoutInflater)

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
        setFilePickerLauncher()
        setListeners()
        return dialog
    }

    private fun setPasswordInput() {
        binding.importMethodGroup.setOnCheckedChangeListener { _, _ ->
            if (binding.radioEncrypted.isChecked) {
                binding.passwordSection.visibility = View.VISIBLE
            } else {
                binding.passwordSection.visibility = View.GONE
            }
        }
    }

    private fun setFilePickerLauncher() {
        filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data
                    uri?.let {
                        file = uri.toFile()
                        binding.filePathInput.setText(file?.name)
                    }
                }
            }
    }

    private fun setListeners() {
        binding.selectFileButton.setOnClickListener {
            val fileType = if (binding.radioEncrypted.isChecked) ENCRYPTED else CSV
            openFile(null, fileType)
        }

        binding.btnImport.setOnClickListener {
            val isEncrypted = binding.radioEncrypted.isChecked
            val password = binding.passwordInput.text.toString()

            if (isEncrypted && password.isBlank()) {
                showMissingPasswordToast()
                return@setOnClickListener
            }
            if (file == null) showMissingFileToast() else handleFile(file!!.toUri())
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

    private fun showMissingFileToast() {
        Toast.makeText(
            context,
            getString(R.string.missing_file_toast),
            Toast.LENGTH_LONG
        ).show()
    }


    private fun openFile(initialUri: Uri?, fileType: String) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = fileType
            initialUri?.let {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri)
            }
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
        }else if (binding.radioCsv.isChecked){
            viewModel.insertAllPasswords(uri)
        }

    }

    private fun showWrongPasswordToast() {
        Toast.makeText(
            context,
            getString(R.string.wrong_password_toast),
            Toast.LENGTH_LONG
        ).show()
    }
}