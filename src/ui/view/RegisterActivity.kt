package ui.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.view.WindowCompat
import com.enekocm.securevault.R
import dagger.hilt.android.AndroidEntryPoint
import ui.compose.screens.RegisterScreen
import ui.compose.theme.SecureVaultTheme
import ui.viewmodel.RegisterViewModel

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            @Suppress("DEPRECATION")
            window.statusBarColor = getColor(R.color.light_red)
        }
        setContent {
            SecureVaultTheme {
                val strength by viewModel.passwordStrength.observeAsState()
                RegisterScreen(
                    strength = strength,
                    onPasswordChange = { viewModel.calculateStrength(it) },
                    onContinue = { master, confirm ->
                        when {
                            master.isEmpty() -> {
                                Toast.makeText(this, getString(R.string.missing_password_toast), Toast.LENGTH_LONG).show()
                            }
                            master != confirm -> {
                                Toast.makeText(this, getString(R.string.different_password_toast), Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                viewModel.createAppKey(master)
                                if (viewModel.isKeyConfigured()) {
                                    biometricActivity()
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    private fun biometricActivity() {
        val intent = Intent(this, BiometricActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}
