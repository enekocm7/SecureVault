package com.enekocm.securevault.ui.view.dialogs

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.enekocm.securevault.R
import com.enekocm.securevault.databinding.CreateMasterPasswordBinding
import com.enekocm.securevault.domain.model.PasswordStrength
import com.enekocm.securevault.ui.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeMasterPasswordDialog: DialogFragment() {
    private lateinit var binding: CreateMasterPasswordBinding

    private val viewModel: RegisterViewModel by viewModels()

    private val label = "Strength:"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = CreateMasterPasswordBinding.inflate(layoutInflater)

        binding.root.background = androidx.core.content.ContextCompat.getDrawable(requireContext(), R.drawable.dialog_background)

        val newMarginTopInDp = 10
        val layoutParams = binding.titleText.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = (newMarginTopInDp * resources.displayMetrics.density).toInt()
        binding.titleText.layoutParams = layoutParams

        binding.titleText.text = getString(R.string.change_master_password)
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

        setObserver()
        setListeners()
        setAfterTextChanged()
        return dialog
    }

    private fun changeProgressBar(strength: PasswordStrength) {
        val text = label + " " + strength.label
        binding.passwordStrengthLabel.text = text
        binding.passwordStrengthBar.progress = strength.progress
        binding.passwordStrengthBar.progressTintList = ColorStateList.valueOf(strength.colorInt)
    }

    private fun setObserver() {
        viewModel.passwordStrength.observe(this) { strength ->
            changeProgressBar(strength)
        }
    }

    private fun setListeners() {
        binding.continueButton.setOnClickListener {
            val masterPassword = binding.masterPasswordInput.text.toString()
            val confirmPassword = binding.confirmPasswordInput.text.toString()
            when {
                masterPassword.isEmpty() -> {
                    Toast.makeText(requireContext(), "Password cannot be empty", Toast.LENGTH_LONG).show()
                }

                masterPassword != confirmPassword -> {
                    Toast.makeText(requireContext(), "The passwords must be the same", Toast.LENGTH_LONG).show()
                }

                else -> {
                    viewModel.createAppKey(masterPassword)
                    if (viewModel.isKeyConfigured()) {
                        dismiss()
                        Toast.makeText(context,getString(R.string.password_changed),Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    private fun setAfterTextChanged() {
        binding.masterPasswordInput.doAfterTextChanged {
            viewModel.calculateStrength(binding.masterPasswordInput.text.toString())
        }
    }
}
