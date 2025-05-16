package com.example.securevault.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.securevault.databinding.ScreenHomeBinding
import com.example.securevault.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ScreenHomeBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true
        binding = ScreenHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isLoggedIn()) {
            login()
        } else {
            setListeners()
        }

    }

    private fun setListeners() {
        binding.registerButton.setOnClickListener {
            if (isLoggedIn()) {
                login()
            } else {
                startActivity(Intent(this, RegisterActivity::class.java))
                finishAffinity()
            }
        }
    }

    private fun isLoggedIn(): Boolean {
        return viewModel.isKeyConfigured()
    }

    private fun login() {
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }
}