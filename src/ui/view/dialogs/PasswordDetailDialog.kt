package ui.view.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import domain.model.PasswordDto
import ui.compose.dialogs.PasswordDetailDialogContent
import ui.compose.theme.SecureVaultTheme
import ui.viewmodel.dialogs.PasswordDetailViewModel
import ui.viewmodel.passwordDetailFactory

class PasswordDetailDialog(private val passwordDto: PasswordDto) : DialogFragment() {

    private val viewModel: PasswordDetailViewModel by viewModels { passwordDetailFactory(requireActivity().application) }

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
                        PasswordDetailDialogContent(
                            initial = passwordDto,
                            onSave = { password ->
                                if (passwordDto.name == password.name) viewModel.savePassword(password)
                                else viewModel.savePassword(passwordDto.name, password)
                                dismiss()
                            },
                            onDelete = { password ->
                                viewModel.removePassword(password)
                                dismiss()
                            },
                            onClose = { dismiss() }
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
