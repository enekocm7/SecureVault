package com.example.securevault.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.securevault.databinding.LoginScreenBinding
import com.example.securevault.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginScreenBinding

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        binding = LoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
        setObserver()
    }

    private fun setObserver() {
        lifecycleScope.launch {
            viewModel.biometricLoginState.collect {
                    success ->
                if (success == false) {
                    Toast.makeText(this@LoginActivity, "Invalid biometric authentication", Toast.LENGTH_SHORT).show()
                } else if (success == true) {
                    skip()
                }
            }
        }
    }


    fun setListeners(){
        binding.loginButton.setOnClickListener {
            viewModel.login(binding.passwordInput.text.toString()).let { result ->
                if (result) {
                    skip()
                } else {
                    Toast.makeText(this,"Invalid password", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.biometricButton.setOnClickListener {
            if (viewModel.isBiometricKeyConfigured()) {
                viewModel.login(this)
            } else {
                Toast.makeText(this, "Biometric authentication not configured", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun skip(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}