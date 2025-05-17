package com.example.securevault.ui.view.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.securevault.databinding.PasswordGeneratorBinding
import com.example.securevault.ui.viewmodel.fragments.GeneratePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeneratePasswordDialog : DialogFragment() {

    private lateinit var binding: PasswordGeneratorBinding
    private val viewModel: GeneratePasswordViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = PasswordGeneratorBinding.inflate(layoutInflater)

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