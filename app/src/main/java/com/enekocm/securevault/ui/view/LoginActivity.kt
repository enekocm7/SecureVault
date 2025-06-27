package com.enekocm.securevault.ui.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.autofill.Dataset
import android.view.View
import android.view.autofill.AutofillId
import android.view.autofill.AutofillManager
import android.view.autofill.AutofillValue
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.enekocm.securevault.R
import com.enekocm.securevault.data.autofill.utils.Fetch
import com.enekocm.securevault.databinding.DialogAccountSelectionBinding
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
    private lateinit var webDomain: String

    private var isAutofill: Boolean = false
    private var hasSuccessfulLogin = false

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
            appPackage = intent.getStringExtra("package") ?: ""
            webDomain = intent.getStringExtra("webDomain") ?: ""
            true
        } catch (_: Exception) {
            false
        }

        if (viewModel.isBiometricKeyConfigured()) {
            viewModel.login(this)
        } else {
            binding.biometricButton.visibility = View.GONE
        }

        setupAccountCard()
        setListeners()
        setObserversPassword()
        setObserversBiometric()
        setObserversAccount()
    }

    private fun setupAccountCard() {
        viewModel.getCurrentUser()?.let { user ->
            user.photoUrl?.let { photoUrl ->
                Glide.with(this)
                    .load(photoUrl)
                    .circleCrop()
                    .into(binding.profileImage)
            }

        } ?: run {
            binding.profileImage.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_account_circle))
        }
    }

    private fun setObserversAccount() {
        lifecycleScope.launch {
            viewModel.currentUser.collect { user ->
                if (user != null) {
                    user.photoUrl?.let { photoUrl ->
                        Glide.with(this@LoginActivity)
                            .load(photoUrl)
                            .circleCrop()
                            .into(binding.profileImage)
                    }
                } else {
                    binding.profileImage.setImageDrawable(AppCompatResources.getDrawable(this@LoginActivity, R.drawable.ic_account_circle))
                }
            }
        }

        lifecycleScope.launch {
            viewModel.accountSwitchResult.collect { success ->
                when (success) {
                    true -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "Account switched successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    false -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "Failed to switch account",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    null -> { /* Initial state */
                    }
                }
            }
        }
    }

    private fun setObserversPassword() {
        lifecycleScope.launch {
            viewModel.passwordLoginState
                .collect { success ->
                    if (success == true) {
                        hasSuccessfulLogin = true
                        if (isAutofill) autofill() else skip()
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
                    hasSuccessfulLogin = true
                    if (isAutofill) autofill() else skip()
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
                Toast.makeText(
                    this,
                    getString(R.string.biometric_authentication_not_configured), Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        binding.accountCard.setOnClickListener {
            showAccountSelectionDialog()
        }
    }

    private fun showAccountSelectionDialog() {
        val dialogBinding = DialogAccountSelectionBinding.inflate(layoutInflater)

        val currentUser = viewModel.getCurrentUser()

        if (currentUser != null) {
            val accountProfileImage = dialogBinding.accountProfileImage
            val accountName = dialogBinding.accountName
            val accountEmail = dialogBinding.accountEmail

            accountName.text = currentUser.displayName
            accountEmail.text = currentUser.email
            Glide.with(this).load(currentUser.photoUrl).into(accountProfileImage)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.addAccountButton.setOnClickListener {
            lifecycleScope.launch {
                val success = viewModel.switchToAccount(this@LoginActivity)
                if (success) {
                    dialog.dismiss()
                    Toast.makeText(
                        this@LoginActivity,
                        "Account added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Failed to add account",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialogBinding.logoutButton.setOnClickListener {
            viewModel.logout()
            dialog.dismiss()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun skip() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    @Suppress("DEPRECATION")
    private fun autofill() {
        val domain = webDomain.takeIf { it.isNotEmpty() }

        val credentials = Fetch.fetchPassword(appPackage, viewModel.getPasswords(), domain) ?: run {
            Toast.makeText(
                this,
                getString(R.string.no_matching_passwords),
                Toast.LENGTH_SHORT
            ).show()
            finishAndRemoveTask()
            return
        }

        val dataset = Dataset.Builder()
            .setValue(usernameId, AutofillValue.forText(credentials.username))
            .setValue(passwordId, AutofillValue.forText(credentials.password))
            .build()

        val resultIntent = Intent().apply {
            putExtra(AutofillManager.EXTRA_AUTHENTICATION_RESULT, dataset)
        }

        setResult(RESULT_OK, resultIntent)
        finishAndRemoveTask()
    }
}