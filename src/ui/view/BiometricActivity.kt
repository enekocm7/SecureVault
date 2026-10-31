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
import domain.model.BiometricResult
import ui.compose.screens.BiometricScreen
import ui.compose.theme.SecureVaultTheme
import ui.viewmodel.BiometricViewModel
import ui.viewmodel.biometricFactory

class BiometricActivity : AppCompatActivity() {

    private val viewModel: BiometricViewModel by viewModels { biometricFactory(application) }
    private var isFromSettings: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            @Suppress("DEPRECATION")
            window.statusBarColor = getColor(R.color.light_red)
        }
        isFromSettings = intent.getStringExtra("settings") == null

        setContent {
            SecureVaultTheme {
                val result by viewModel.authenticationState.observeAsState()
                val (text, enabled) = when (result) {
                    is BiometricResult.AuthenticationError -> getString(R.string.try_again) to true
                    is BiometricResult.AuthenticationNotRecognized -> getString(R.string.authentication_not_recognized) to false
                    is BiometricResult.FeatureUnavailable -> getString(R.string.feature_unavailable) to false
                    is BiometricResult.HardwareNotAvailable -> getString(R.string.hardware_unavailable) to false
                    is BiometricResult.AuthenticationFailed -> "" to true
                    else -> "" to true
                }
                if (result is BiometricResult.AuthenticationSuccess) {
                    if (isFromSettings) skip() else skipCloud()
                }
                BiometricScreen(
                    buttonText = text,
                    buttonEnabled = enabled,
                    onEnable = {
                        if (!viewModel.enableBiometric(this)) {
                            Toast.makeText(this, R.string.no_biometric_key, Toast.LENGTH_LONG).show()
                            if (isFromSettings) skip() else skipCloud()
                        }
                    },
                    onSkip = { if (isFromSettings) skip() else skipCloud() }
                )
            }
        }
    }

    private fun skipCloud() {
        val intent = Intent(this, CloudActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun skip() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}
