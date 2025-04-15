package com.example.securevault.ui.view

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.widget.doAfterTextChanged
import com.example.securevault.databinding.CreateMasterPasswordBinding
import com.example.securevault.domain.entities.PasswordStrength
import com.example.securevault.ui.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: CreateMasterPasswordBinding

    private val viewModel: RegisterViewModel by viewModels()
    private val label = "Strength:"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        binding = CreateMasterPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setObserver()
        setListener()
        setAfterTextChanged()

    }

    private fun changeProgressBar(strength: PasswordStrength) {
            val text = label+" "+strength.label
            binding.passwordStrengthLabel.text = text
            binding.passwordStrengthBar.progress = strength.progress
            binding.passwordStrengthBar.progressTintList = ColorStateList.valueOf(strength.colorInt)
    }

    private fun setObserver(){
        viewModel.passwordStrength.observe(this) {
                strength -> changeProgressBar(strength)
        }
    }

    private fun setListener(){
        binding.continueButton.setOnClickListener {
            val masterPassword = binding.masterPasswordInput.text.toString()
            val confirmPassword = binding.confirmPasswordInput.text.toString()

            when {
                masterPassword.isEmpty() -> {
                    Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_LONG).show()
                }
                masterPassword != confirmPassword -> {
                    Toast.makeText(this, "The passwords must be the same", Toast.LENGTH_LONG).show()
                }
                else -> {
                    viewModel.createAppKey(masterPassword)
                    if (viewModel.isKeyConfigured()) {
                        homeActivity()
                    }
                }
            }
        }
    }

    private fun setAfterTextChanged(){
        binding.masterPasswordInput.doAfterTextChanged {
            viewModel.calculateStrength(binding.masterPasswordInput.text.toString())
        }
    }

    private fun homeActivity(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}


