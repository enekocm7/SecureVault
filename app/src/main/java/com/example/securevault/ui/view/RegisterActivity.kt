package com.example.securevault.ui.view

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.securevault.databinding.ActivityMasterPasswordBinding
import com.example.securevault.domain.entities.PasswordStrength
import com.example.securevault.ui.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMasterPasswordBinding

    private val viewModel: RegisterViewModel by viewModels()
    private val label = "Strength:"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.passwordStrength.observe(this) {
            strength -> changeProgressBar(strength)
        }

        binding.continueButton.setOnClickListener {

        }

        binding.masterPasswordInput.doAfterTextChanged {
            viewModel.calculateStrength(binding.masterPasswordInput.text.toString())
        }

    }

    private fun changeProgressBar(strength: PasswordStrength) {
            val text = label+" "+strength.label
            binding.passwordStrengthLabel.text = text
            binding.passwordStrengthBar.progress = strength.progress
            binding.passwordStrengthBar.progressTintList =
                ColorStateList.valueOf(strength.colorInt)
    }
}


