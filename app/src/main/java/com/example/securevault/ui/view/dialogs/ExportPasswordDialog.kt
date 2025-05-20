package com.example.securevault.ui.view.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.example.securevault.databinding.DialogExportMethodBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExportPasswordDialog : DialogFragment() {

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

        return dialog
    }

}