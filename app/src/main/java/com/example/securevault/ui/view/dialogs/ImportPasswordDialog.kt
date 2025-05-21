package com.example.securevault.ui.view.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.example.securevault.databinding.DialogImportFileBinding

class ImportPasswordDialog : DialogFragment() {

    private lateinit var binding: DialogImportFileBinding

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
}