package com.example.securevault.ui.view.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.securevault.databinding.CreatePasswordDialogBinding
import com.example.securevault.ui.viewmodel.CreatePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePasswordDialog : DialogFragment() {

    private lateinit var binding: CreatePasswordDialogBinding
    private val viewModel : CreatePasswordViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = CreatePasswordDialogBinding.inflate(layoutInflater)

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

        setListeners()
        return dialog
    }


    private fun setListeners() {
        binding.saveButton.setOnClickListener {

        }

        binding.cancelButton.setOnClickListener {
            this.dismiss()
        }
    }
}

