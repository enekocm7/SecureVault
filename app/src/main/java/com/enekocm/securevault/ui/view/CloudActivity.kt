package com.enekocm.securevault.ui.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.enekocm.securevault.R
import com.enekocm.securevault.databinding.CloudSuggestionBinding
import com.enekocm.securevault.domain.model.AuthState
import com.enekocm.securevault.ui.viewmodel.CloudViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CloudActivity : AppCompatActivity() {

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
        observeViewModel()
    }

    private fun setListeners() {
        binding.skipTextButton.setOnClickListener {
            skip()
        }
        binding.googleSignInButton.setOnClickListener {
            viewModel.signInWithGoogle(this)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Authenticated -> {
                        Toast.makeText(
                            this@CloudActivity,
                            "Successfully signed in with Google!",
                            Toast.LENGTH_SHORT
                        ).show()
                        skip()
                    }

                    is AuthState.Unauthenticated -> {
                        // User is not signed in, stay on current screen
                    }

                    is AuthState.Initial -> {
                        // Initial state, do nothing
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.googleSignInButton.isEnabled = !isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { error ->
                error?.let {
                    Toast.makeText(this@CloudActivity, it, Toast.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }
    }


    private fun skip() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}
