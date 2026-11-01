package ui.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.autofill.Dataset
import android.view.autofill.AutofillId
import android.view.autofill.AutofillManager
import android.view.autofill.AutofillValue
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.enekocm.securevault.R
import data.autofill.utils.Fetch
import ui.compose.dialogs.AccountDialogContent
import ui.compose.screens.LoginScreen
import ui.compose.theme.SecureVaultTheme
import ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var usernameId: AutofillId
    private lateinit var passwordId: AutofillId
    private lateinit var appPackage: String
    private lateinit var webDomain: String
    private var isAutofill: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            @Suppress("DEPRECATION")
            window.statusBarColor = getColor(R.color.light_red)
        }

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
        }

        lifecycleScope.launch {
            viewModel.passwordLoginState.collect { success ->
                if (success == true) {
                    if (isAutofill) autofill() else skip()
                } else if (success == false) {
                    Toast.makeText(this@LoginActivity, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.biometricLoginState.collect { success ->
                if (success == true) {
                    if (isAutofill) autofill() else skip()
                } else if (success == false) {
                    Toast.makeText(this@LoginActivity, getString(R.string.invalid_biometric_authentication), Toast.LENGTH_SHORT).show()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.accountSwitchResult.collect { success ->
                when (success) {
                    true -> Toast.makeText(this@LoginActivity, "Account switched successfully", Toast.LENGTH_SHORT).show()
                    false -> Toast.makeText(this@LoginActivity, "Failed to switch account", Toast.LENGTH_SHORT).show()
                    null -> Unit
                }
            }
        }

        setContent {
            SecureVaultTheme {
                val user by viewModel.currentUser.collectAsState()
                var showAccount by remember { mutableStateOf(false) }
                LoginScreen(
                    photoUrl = user?.photoUrl?.toString(),
                    showBiometric = viewModel.isBiometricKeyConfigured(),
                    onLogin = { viewModel.login(it) },
                    onBiometric = {
                        if (viewModel.isBiometricKeyConfigured()) viewModel.login(this)
                        else Toast.makeText(this, getString(R.string.biometric_authentication_not_configured), Toast.LENGTH_SHORT).show()
                    },
                    onAccountClick = { showAccount = true }
                )
                if (showAccount) {
                    val current = viewModel.getCurrentUser()
                    AccountDialogContent(
                        displayName = current?.displayName,
                        email = current?.email,
                        photoUrl = current?.photoUrl?.toString(),
                        onSwitch = {
                            lifecycleScope.launch {
                                val success = viewModel.switchToAccount(this@LoginActivity)
                                if (success) {
                                    showAccount = false
                                    Toast.makeText(this@LoginActivity, "Account added successfully", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@LoginActivity, "Failed to add account", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onLogout = {
                            viewModel.logout()
                            showAccount = false
                            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        },
                        onDismiss = { showAccount = false }
                    )
                }
            }
        }
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
            Toast.makeText(this, getString(R.string.no_matching_passwords), Toast.LENGTH_SHORT).show()
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
