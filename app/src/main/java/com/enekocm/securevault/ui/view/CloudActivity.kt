package com.enekocm.securevault.ui.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.enekocm.securevault.R
import com.enekocm.securevault.databinding.CloudSuggestionBinding
import com.enekocm.securevault.ui.viewmodel.CloudViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CloudActivity: AppCompatActivity() {

    private lateinit var binding: CloudSuggestionBinding

    private val viewModel: CloudViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            @Suppress("DEPRECATION")
            window.statusBarColor = getColor(R.color.light_red)
        }
        binding = CloudSuggestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners() {
        binding.skipTextButton.setOnClickListener {
            skip()
        }
        binding.googleSignInButton.setOnClickListener {

        }
    }
    private fun skip() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}


