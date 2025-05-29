package com.enekocm.securevault.ui.view.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.enekocm.securevault.databinding.CreatePasswordDialogBinding
import com.enekocm.securevault.domain.model.PasswordDto
import com.enekocm.securevault.ui.view.HomeActivity
import com.enekocm.securevault.ui.viewmodel.dialogs.CreatePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePasswordDialog(private val supportFragmentManager: FragmentManager) : DialogFragment() {

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
                setFragmentResult(HomeActivity.PASSWORD_RELOAD_REQUEST_KEY, bundleOf())
                this.dismiss()
            }
        }

        binding.cancelButton.setOnClickListener {
            this.dismiss()
        }

        binding.generatePasswordButton.setOnClickListener {
            GeneratePasswordDialog().show(supportFragmentManager, "Generate Password")
        }
        supportFragmentManager.setFragmentResultListener(
            GeneratePasswordDialog.REQUEST_KEY,
            this
        ) { _, bundle ->
            val generatedPassword = bundle.getString("password")
            if (!generatedPassword.isNullOrEmpty()) binding.passwordEditText.setText(generatedPassword)

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
            url = fields["url"].toString().lowercase(),
            username = fields["username"].toString(),
            value = fields["password"].toString()
        )
    }

    private fun showToast(field: String) {
        Toast.makeText(requireContext(), "$field can not be empty", Toast.LENGTH_LONG).show()
    }
}

