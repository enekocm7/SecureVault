package com.enekocm.securevault.ui.view.dialogs

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
import com.enekocm.securevault.databinding.PasswordDetailBinding
import com.enekocm.securevault.domain.model.PasswordDto
import com.enekocm.securevault.ui.view.HomeActivity
import com.enekocm.securevault.ui.viewmodel.dialogs.PasswordDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordDetailDialog(private val passwordDto: PasswordDto): DialogFragment() {

    private lateinit var binding: PasswordDetailBinding
    private val viewModel: PasswordDetailViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = PasswordDetailBinding.inflate(layoutInflater)

        val displayMetrics =resources.displayMetrics
        val width =(displayMetrics.widthPixels * 0.9).toInt()

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
        setContent()

        setListeners()
        return dialog
    }

    private fun setContent(){
        binding.nameDetailEditText.setText(passwordDto.name)
        binding.urlDetailEditText.setText(passwordDto.url)
        binding.usernameDetailEditText.setText(passwordDto.username)
        binding.passwordDetailEditText.setText(passwordDto.value)
    }

    private fun setListeners() {
        binding.saveButton.setOnClickListener {
            val password = getPassword() ?: return@setOnClickListener

            if (passwordDto.name == password.name) {
                viewModel.savePassword(password)
            } else {
                viewModel.savePassword(passwordDto.name, password)
            }

            setFragmentResult(HomeActivity.PASSWORD_RELOAD_REQUEST_KEY, bundleOf())
            this.dismiss()
        }

        binding.deleteButton.setOnClickListener {
            val password = getPassword()
            if (password!=null){
                viewModel.removePassword(password)
                setFragmentResult(HomeActivity.PASSWORD_RELOAD_REQUEST_KEY, bundleOf())
                this.dismiss()
            }
        }

        binding.closeButton.setOnClickListener {
            this.dismiss()
        }
    }

    private fun getPassword(): PasswordDto? {
        val fields = mapOf(
            "name" to binding.nameDetailEditText.text.toString(),
            "url" to binding.urlDetailEditText.text.toString(),
            "username" to binding.usernameDetailEditText.text.toString(),
            "password" to binding.passwordDetailEditText.text.toString()
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