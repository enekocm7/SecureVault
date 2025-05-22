package com.example.securevault.ui.view.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.example.securevault.databinding.DialogExportMethodBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExportPasswordDialog : DialogFragment() {

    companion object {
        private const val ENCRYPTED = "application/sv"
        private const val CSV = "application/csv"
    }

    private lateinit var binding: DialogExportMethodBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogExportMethodBinding.inflate(layoutInflater)

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

    private fun setFilePickerLauncher() {

    }

    private fun setListeners() {
        binding.browseButton.setOnClickListener {

        }
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


}