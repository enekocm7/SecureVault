package ui.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.enekocm.securevault.R
import domain.model.AuthState
import ui.compose.screens.CloudScreen
import ui.compose.theme.SecureVaultTheme
import ui.viewmodel.CloudViewModel
import ui.viewmodel.cloudFactory
import kotlinx.coroutines.launch

class CloudActivity : AppCompatActivity() {

    private val viewModel: CloudViewModel by viewModels { cloudFactory(application) }
    private var login: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            @Suppress("DEPRECATION")
            window.statusBarColor = getColor(R.color.light_red)
        }
        login = intent.getBooleanExtra("login", false)

        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Authenticated -> {
                        Toast.makeText(this@CloudActivity, "Successfully signed in with Google!", Toast.LENGTH_SHORT).show()
                        if (login) loginIntent() else skip()
                    }
                    else -> Unit
                }
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

        setContent {
            SecureVaultTheme {
                val isLoading by viewModel.isLoading.collectAsState()
                CloudScreen(
                    isLoginMode = login,
                    signInEnabled = !isLoading,
                    onGoogleSignIn = {
                        lifecycleScope.launch {
                            viewModel.signInWithGoogle(this@CloudActivity, allowNewAccounts = !login)
                        }
                    },
                    onSkip = { skip() }
                )
            }
        }
    }

    private fun skip() {
        if (login) {
            finish()
        } else {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    private fun loginIntent() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}
