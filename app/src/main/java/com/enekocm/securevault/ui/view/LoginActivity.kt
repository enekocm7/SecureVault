package com.enekocm.securevault.ui.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.autofill.Dataset
import android.view.autofill.AutofillId
import android.view.autofill.AutofillManager
import android.view.autofill.AutofillValue
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.enekocm.securevault.R
import com.enekocm.securevault.data.autofill.Fetch
import com.enekocm.securevault.databinding.LoginScreenBinding
import com.enekocm.securevault.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginScreenBinding

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var usernameId: AutofillId
    private lateinit var passwordId: AutofillId
    private lateinit var appPackage: String

    private var isAutofill : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            @Suppress("DEPRECATION")
            window.statusBarColor = getColor(R.color.light_red)
        }
        binding = LoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        isAutofill = try {
            usernameId = intent.getParcelableExtra("usernameId")!!
            passwordId = intent.getParcelableExtra("passwordId")!!
            appPackage = intent.getStringExtra("package")!!
            true
        }catch (_: Exception){
            false
        }

        if (viewModel.isBiometricKeyConfigured()) {
            viewModel.login(this)
        }
        setListeners()
        setObserversPassword()
        setObserversBiometric()
    }

    private fun setObserversPassword() {
        lifecycleScope.launch {
            viewModel.passwordLoginState
                .collect { success ->
                    if (success == true) {
                        if(isAutofill) autofill() else skip()
                    } else if (success == false) {
                        Toast.makeText(
                            this@LoginActivity,
                            getString(R.string.invalid_password),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun setObserversBiometric() {
        lifecycleScope.launch {
            viewModel.biometricLoginState.collect { success ->
                if (success == true) {
                    if(isAutofill) autofill() else skip()
                } else if (success == false) {
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.invalid_biometric_authentication),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun setListeners() {
        binding.loginButton.setOnClickListener {
            viewModel.login(binding.passwordInput.text.toString())
        }
        binding.biometricButton.setOnClickListener {
            if (viewModel.isBiometricKeyConfigured()) {
                viewModel.login(this)
            } else {
                Toast.makeText(this,
                    getString(R.string.biometric_authentication_not_configured), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun skip() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    @Suppress("DEPRECATION")
    private fun autofill(){
        val (username, password) = Fetch.fetchPassword(appPackage,viewModel.getPasswords())

        val dataset = Dataset.Builder()
            .setValue(usernameId, AutofillValue.forText(username))
            .setValue(passwordId, AutofillValue.forText(password))
            .build()

        val resultIntent = Intent().apply {
            putExtra(AutofillManager.EXTRA_AUTHENTICATION_RESULT, dataset)
        }

        setResult(RESULT_OK, resultIntent)
        finishAndRemoveTask()
    }
}