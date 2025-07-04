package com.enekocm.securevault.ui.view.dialogs

import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.enekocm.securevault.R
import com.enekocm.securevault.databinding.DialogExportBinding
import com.enekocm.securevault.ui.viewmodel.dialogs.ExportPasswordViewModel
import com.enekocm.securevault.utils.FilePicker
import com.enekocm.securevault.utils.FilePickerType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExportPasswordDialog : DialogFragment() {

    private lateinit var binding: DialogExportBinding
    private val filePicker = FilePicker(fragment = this) { uri ->
        folderUri = uri
        val fileName = getFolderNameFromUri(uri)
        binding.pathInput.setText(fileName ?: uri.lastPathSegment)
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
            if (folderUri == null) showToast(getString(R.string.missing_folder_toast)) else handleFile(folderUri!!)
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
        filePicker.launch(FilePickerType.FOLDER)
    }

    private fun getFolderNameFromUri(uri: Uri): String? {
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

