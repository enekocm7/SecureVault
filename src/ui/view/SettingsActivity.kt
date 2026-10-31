package ui.view

import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.autofill.AutofillManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.enekocm.securevault.R
import domain.model.AuthState
import ui.compose.screens.SettingsScreen
import ui.compose.theme.SecureVaultTheme
import ui.view.dialogs.ChangeMasterPasswordDialog
import ui.view.dialogs.ExportPasswordDialog
import ui.view.dialogs.ImportPasswordDialog
import ui.viewmodel.SettingsViewModel
import ui.viewmodel.settingsFactory
import utils.FilePicker
import utils.FilePickerType
import com.enekocm.securevault.SecureVaultApp
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModels { settingsFactory(application) }
    private val auth get() = (application as SecureVaultApp).container.firebaseAuth
    private var refreshTick = mutableStateOf(0)

    private val folderPicker = FilePicker(activity = this) { uri ->
        viewModel.setBackupLocation(uri)
        viewModel.enableBackup()
        viewModel.createBackup()
    }
    private val filePicker = FilePicker(activity = this) { uri ->
        viewModel.loadBackup(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            @Suppress("DEPRECATION")
            window.statusBarColor = getColor(R.color.red)
        }

        lifecycleScope.launch {
            viewModel.backup.collect {
                Toast.makeText(
                    this@SettingsActivity,
                    getString(if (it) R.string.backup_created_successfully else R.string.backup_creation_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        lifecycleScope.launch {
            viewModel.loadBackup.collect {
                Toast.makeText(
                    this@SettingsActivity,
                    getString(if (it) R.string.load_backup_successfully else R.string.load_backup_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                if (state is AuthState.Authenticated) {
                    Toast.makeText(this@SettingsActivity, "Successfully signed in with Google!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.errorMessage.collect { error ->
                error?.let {
                    Toast.makeText(this@SettingsActivity, it, Toast.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }

        setContent {
            SecureVaultTheme {
                val tick = refreshTick.value
                val user = auth?.currentUser
                val signedIn = user != null
                val backupEnabled = remember(tick) { mutableStateOf(viewModel.isBackupEnabled()) }
                val backupLoc = if (tick >= 0) viewModel.getBackupLocation() else null
                val showBackup = backupEnabled.value && backupLoc != null

                SettingsScreen(
                    userName = user?.displayName ?: getString(R.string.unknown_user).takeIf { signedIn }
                        ?: getString(R.string.not_signed_in),
                    userEmail = user?.email ?: getString(R.string.no_email).takeIf { signedIn }
                        ?: getString(R.string.sign_in_to_sync),
                    photoUrl = user?.photoUrl?.toString(),
                    syncText = getString(if (signedIn) R.string.sync_enabled else R.string.sync_disabled),
                    isSignedIn = signedIn,
                    biometricChecked = viewModel.isBiometric(),
                    biometricEnabled = !viewModel.isBiometric(),
                    backupEnabled = backupEnabled.value,
                    backupLocationText = backupLoc?.lastPathSegment?.split(':')?.last(),
                    showBackupDetails = showBackup,
                    autofillChecked = hasEnabledAutofill(),
                    showBrowserSwitch = hasEnabledAutofill(),
                    browserChecked = checkBrowserAutofillEnabled(),
                    appVersion = appVersionName(),
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onSignIn = { viewModel.signInWithGoogle(this) },
                    onSignOut = {
                        auth?.signOut()
                        refreshTick.value++
                    },
                    onChangeMaster = { ChangeMasterPasswordDialog().show(supportFragmentManager, "ChangeMasterPassword") },
                    onBiometricChange = {
                        if (it && !viewModel.isBiometric()) {
                            val intent = Intent(this, BiometricActivity::class.java)
                            intent.putExtra("settings", true)
                            startActivity(intent)
                        }
                    },
                    onImport = { ImportPasswordDialog().show(supportFragmentManager, "Import") },
                    onExport = { ExportPasswordDialog().show(supportFragmentManager, "Export") },
                    onClear = {
                        viewModel.clearPasswords()
                        Toast.makeText(this, getString(R.string.passwords_clear_toast), Toast.LENGTH_LONG).show()
                    },
                    onBackupChange = {
                        backupEnabled.value = it
                        if (it) {
                            viewModel.enableBackup()
                            if (viewModel.getBackupLocation() == null) {
                                folderPicker.launch(FilePickerType.FOLDER)
                            }
                        } else {
                            viewModel.disableBackup()
                        }
                    },
                    onBackupLocation = { folderPicker.launch(FilePickerType.FOLDER) },
                    onCreateBackup = { viewModel.createBackup() },
                    onLoadBackup = { filePicker.launch(FilePickerType.FILE, "application/octet-stream") },
                    onAutofillChange = { if (it) requestAutofillService() },
                    onBrowserChange = { if (it) openChromeAutofillSettings() },
                    onPrivacy = {
                        val url = "https://github.com/enekocm7/SecureVault/tree/master/legal"
                        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshTick.value++
    }

    private fun requestAutofillService() {        if (!hasEnabledAutofill()) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE)
                intent.data = "package:${packageName}".toUri()
                startActivity(intent)
                Toast.makeText(this, getString(R.string.please_select_autofill_service), Toast.LENGTH_LONG).show()
            } catch (_: ActivityNotFoundException) {
                startActivity(Intent(Settings.ACTION_SETTINGS))
                Toast.makeText(this, getString(R.string.enable_autofill_in_settings), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun hasEnabledAutofill(): Boolean {
        return getSystemService(AutofillManager::class.java).hasEnabledAutofillServices()
    }

    private fun checkBrowserAutofillEnabled(): Boolean {
        val uri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority("com.android.chrome.AutofillThirdPartyModeContentProvider")
            .path("autofill_third_party_mode")
            .build()
        val cursor = contentResolver.query(uri, arrayOf("autofill_third_party_state"), null, null, null)
            ?: return false
        cursor.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex("autofill_third_party_state")
                if (index != -1) return 0 != it.getInt(index)
            }
        }
        return false
    }

    private fun openChromeAutofillSettings() {
        if (!checkBrowserAutofillEnabled()) {
            val intent = Intent(Intent.ACTION_APPLICATION_PREFERENCES)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addCategory(Intent.CATEGORY_APP_BROWSER)
            intent.addCategory(Intent.CATEGORY_PREFERENCE)
            intent.setPackage("com.android.chrome")
            startActivity(Intent.createChooser(intent, "Pick Chrome Channel"))
        }
    }

    private fun appVersionName(): String {
        return try {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0).versionName ?: "2.0.1"
        } catch (_: Exception) {
            "2.0.1"
        }
    }
}
