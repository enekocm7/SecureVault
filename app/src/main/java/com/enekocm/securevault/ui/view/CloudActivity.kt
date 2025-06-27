package com.enekocm.securevault.ui.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
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

    private var login: Boolean = false

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
        login = intent.getBooleanExtra("login",false)

        if (login){
            changeLoginAppearance()
        }

        setListeners()
        observeViewModel()
    }

    private fun changeLoginAppearance() {
        binding.cloudIcon.visibility = View.INVISIBLE

        binding.titleText.text = getString(R.string.login)
        binding.titleText.textSize = 48f

        binding.descriptionText.visibility = View.VISIBLE
        binding.descriptionText.textSize = 24f
        binding.descriptionText.text = getString(R.string.login_description)

        binding.skipTextButton.text = getString(R.string.cancel)
    }

    private fun setListeners() {
        binding.skipTextButton.setOnClickListener {
            skip()
        }
        binding.googleSignInButton.setOnClickListener {
            viewModel.signInWithGoogle(this, allowNewAccounts = !login)
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
                        if (login){
                            loginIntent()
                        }else{
                            skip()
                        }
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
        if (login){
            finish()
        }else{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    private fun loginIntent(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}
