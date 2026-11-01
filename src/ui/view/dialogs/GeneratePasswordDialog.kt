package ui.view.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.setFragmentResult
import dagger.hilt.android.AndroidEntryPoint
import ui.compose.dialogs.GeneratePasswordDialogContent
import ui.compose.theme.SecureVaultTheme
import ui.viewmodel.dialogs.GeneratePasswordViewModel

@AndroidEntryPoint
class GeneratePasswordDialog : DialogFragment() {

    private val viewModel: GeneratePasswordViewModel by viewModels()

    companion object {
        const val REQUEST_KEY = "GENERATED_PASSWORD_REQUEST_KEY"
    }

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
                        val password by viewModel.password.collectAsState()
                        val strength by viewModel.passwordStrength.collectAsState()
                        val loading by viewModel.isLoading.collectAsState()
                        LaunchedEffect(Unit) { viewModel.getPassword(16, true, true, true, true) }
                        GeneratePasswordDialogContent(
                            generated = password,
                            strength = strength,
                            isLoading = loading,
                            onRegenerate = { viewModel.getPassword(16, true, true, true, true) },
                            onUse = {
                                setFragmentResult(REQUEST_KEY, bundleOf("password" to it))
                                dismiss()
                            },
                            onClose = { dismiss() },
                            onGeneratePassword = { l, lo, up, n, s -> viewModel.getPassword(l, lo, up, n, s) },
                            onGeneratePassphrase = { w -> viewModel.getPassphrase(w, "-") }
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
