package com.enekocm.securevault.ui.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.enekocm.securevault.R
import com.enekocm.securevault.databinding.ScreenHomeBinding
import com.enekocm.securevault.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ScreenHomeBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isLoggedIn()){
            login()
        }
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            @Suppress("DEPRECATION")
            window.statusBarColor = getColor(R.color.light_red)
        }
        binding = ScreenHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners() {
        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, CloudActivity::class.java)
            intent.putExtra("login",true)
            startActivity(intent)
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