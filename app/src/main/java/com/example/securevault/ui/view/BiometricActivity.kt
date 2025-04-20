package com.example.securevault.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.securevault.R
import com.example.securevault.databinding.BiometricSugestionBinding
import com.example.securevault.domain.model.BiometricResult
import com.example.securevault.ui.viewmodel.BiometricViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BiometricActivity : AppCompatActivity() {

    private lateinit var binding: BiometricSugestionBinding

    private val viewModel : BiometricViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        binding = BiometricSugestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
        setObservers()

    }

    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.authenticationState.collect {
                result ->
                when(result){
                    is BiometricResult.AuthenticationSuccess -> {

                    }
                    is BiometricResult.AuthenticationError -> {
                        binding.enableButton.isEnabled = true
                        binding.enableButton.text = getString(R.string.try_again)
                    }
                    is BiometricResult.AuthenticationNotRecognized -> {}

                    is BiometricResult.FeatureUnavailable -> {
                        binding.enableButton.isEnabled = false
                        binding.enableButton.text = getString(R.string.feature_unavailable)
                    }
                    is BiometricResult.HardwareNotAvailable -> {
                        binding.enableButton.isEnabled = false
                        binding.enableButton.text = getString(R.string.hardware_unavailable)
                    }
                    is BiometricResult.AuthenticationFailed -> {
                        binding.enableButton.isEnabled = true
                    }
                    null -> {}
                }
            }
        }
    }

    private fun setListeners(){
        binding.enableButton.setOnClickListener {
            viewModel.enableBiometric(this)
        }

        binding.skipTextButton.setOnClickListener {
            skip()
        }
    }

    private fun skip() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}


