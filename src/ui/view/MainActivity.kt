package ui.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.enekocm.securevault.R
import dagger.hilt.android.AndroidEntryPoint
import ui.compose.screens.WelcomeScreen
import ui.compose.theme.SecureVaultTheme
import ui.viewmodel.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isLoggedIn()) {
            login()
            return
        }
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            @Suppress("DEPRECATION")
            window.statusBarColor = getColor(R.color.light_red)
        }
        setContent {
            SecureVaultTheme {
                WelcomeScreen(
                    onLogin = {
                        val intent = Intent(this, CloudActivity::class.java)
                        intent.putExtra("login", true)
                        startActivity(intent)
                    },
                    onRegister = {
                        startActivity(Intent(this, RegisterActivity::class.java))
                    }
                )
            }
        }
    }

    private fun isLoggedIn(): Boolean = viewModel.isKeyConfigured()

    private fun login() {
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }
}
