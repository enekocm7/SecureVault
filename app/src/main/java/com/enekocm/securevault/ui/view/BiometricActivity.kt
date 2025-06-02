package com.enekocm.securevault.ui.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.enekocm.securevault.R
import com.enekocm.securevault.databinding.BiometricSugestionBinding
import com.enekocm.securevault.domain.model.BiometricResult
import com.enekocm.securevault.ui.viewmodel.BiometricViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiometricActivity : AppCompatActivity() {

    private lateinit var binding: BiometricSugestionBinding

    private val viewModel: BiometricViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            @Suppress("DEPRECATION")
            window.statusBarColor = getColor(R.color.light_red)
        }
        binding = BiometricSugestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
        setObservers()
    }

    private fun setObservers() {
        viewModel.authenticationState.observe(this) { result ->
            when (result) {
                is BiometricResult.AuthenticationSuccess -> {
                    skip()
                }

                is BiometricResult.AuthenticationError -> {
                    binding.enableButton.isEnabled = true
                    binding.enableButton.text = getString(R.string.try_again)
                }

                is BiometricResult.AuthenticationNotRecognized -> {
                    binding.enableButton.isEnabled = false
                    binding.enableButton.text = getString(R.string.authentication_not_recognized)
                }

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

                null -> {
                    //Nothing
                }
            }
        }

    }

    private fun setListeners() {
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


