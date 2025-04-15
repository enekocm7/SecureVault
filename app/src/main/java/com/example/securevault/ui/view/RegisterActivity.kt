package com.example.securevault.ui.view

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
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
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        binding = ActivityMasterPasswordBinding.inflate(layoutInflater)
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
            if (binding.masterPasswordInput.text.toString() == binding.confirmPasswordInput.text.toString()){
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }else{
                Toast.makeText(this,"The passwords must be the same", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setAfterTextChanged(){
        binding.masterPasswordInput.doAfterTextChanged {
            viewModel.calculateStrength(binding.masterPasswordInput.text.toString())
        }
    }
}


