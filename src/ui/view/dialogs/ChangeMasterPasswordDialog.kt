package ui.view.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.enekocm.securevault.R
import dagger.hilt.android.AndroidEntryPoint
import ui.compose.screens.RegisterScreen
import ui.compose.theme.SecureVaultTheme
import ui.viewmodel.RegisterViewModel

@AndroidEntryPoint
class ChangeMasterPasswordDialog : DialogFragment() {
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.setContentView(
            ComposeView(requireContext()).apply {
                setContent {
                    SecureVaultTheme {
                        val strength by viewModel.passwordStrength.observeAsState()
                        RegisterScreen(
                            strength = strength,
                            title = getString(R.string.change_master_password),
                            onPasswordChange = { viewModel.calculateStrength(it) },
                            onContinue = { master, confirm ->
                                when {
                                    master.isEmpty() -> Toast.makeText(requireContext(), "Password cannot be empty", Toast.LENGTH_LONG).show()
                                    master != confirm -> Toast.makeText(requireContext(), "The passwords must be the same", Toast.LENGTH_LONG).show()
                                    else -> {
                                        viewModel.createAppKey(master)
                                        if (viewModel.isKeyConfigured()) {
                                            viewModel.reloadFirebaseCredentials()
                                            dismiss()
                                            Toast.makeText(context, getString(R.string.password_changed), Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        )
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return dialog
    }
}

@Composable
fun ChangeMasterPasswordComposeDialog(onDismiss: () -> Unit) {
    // Used from SettingsActivity Compose (wraps same RegisterScreen with its own VM via Hilt? simplified: dismiss only)
    // Actual save is handled by caller re-showing ChangeMasterPasswordDialog fragment; this is a placeholder
    // to keep Settings Compose self-contained. Caller should use fragment instead.
    onDismiss()
}
