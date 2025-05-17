package com.example.securevault.ui.view.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.example.securevault.databinding.CreatePasswordDialogBinding
import com.example.securevault.domain.model.PasswordDto
import com.example.securevault.ui.view.HomeActivity
import com.example.securevault.ui.viewmodel.CreatePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePasswordDialog : DialogFragment() {

    private lateinit var binding: CreatePasswordDialogBinding
    private val viewModel: CreatePasswordViewModel by viewModels()

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
            val passwordDto: PasswordDto? = getPassword()
            if (passwordDto != null) {
                viewModel.savePassword(passwordDto)
            }
            setFragmentResult(HomeActivity.PASSWORD_SAVED_REQUEST_KEY, bundleOf())
            this.dismiss()
        }

        binding.cancelButton.setOnClickListener {
            this.dismiss()
        }
    }

    private fun getPassword(): PasswordDto? {
        val fields = mapOf(
            "name" to binding.nameEditText.text.toString(),
            "url" to binding.urlEditText.text.toString(),
            "username" to binding.usernameEditText.text.toString(),
            "password" to binding.passwordEditText.text.toString()
        )

        fields.forEach { (fieldName, value) ->
            if (value.isEmpty()) {
                showToast(fieldName)
                return null
            }
        }

        return PasswordDto(
            name = fields["name"].toString(),
            url = fields["url"].toString(),
            username = fields["username"].toString(),
            value = fields["password"].toString()
        )
    }

    private fun showToast(field: String) {
        Toast.makeText(requireContext(), "$field can not be empty", Toast.LENGTH_LONG).show()
    }
}

