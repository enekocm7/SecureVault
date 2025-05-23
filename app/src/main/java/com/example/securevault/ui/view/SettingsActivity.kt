package com.example.securevault.ui.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.autofill.AutofillManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.example.securevault.R
import com.example.securevault.databinding.SettingsActivityBinding
import com.example.securevault.ui.view.dialogs.ChangeMasterPasswordDialog
import com.example.securevault.ui.view.dialogs.ExportPasswordDialog
import com.example.securevault.ui.view.dialogs.ImportPasswordDialog
import com.example.securevault.ui.viewmodel.SettingsViewModel
import com.example.securevault.utils.FilePicker
import com.example.securevault.utils.FilePickerType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingsActivityBinding
    private val viewModel: SettingsViewModel by viewModels()

    private val folderPicker = FilePicker(activity = this) { uri ->
        viewModel.setBackupLocation(uri)
        viewModel.enableBackup()
        updateBackupLocationText()
        viewModel.createBackup()
    }

    private val filePicker = FilePicker(activity = this) { uri ->
        viewModel.loadBackup(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarSettings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbarSettings.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setListeners()
        checkBiometric()
        observeBackupState()
        updateBackupLocationText()
        checkBackupButtons()
        checkAutofill()
    }

    override fun onResume() {
        super.onResume()
        checkBiometric()
        binding.autoBackup.isChecked = viewModel.isBackupEnabled()
        updateBackupLocationText()
        checkBackupButtons()
        checkAutofill()
    }

    private fun checkBackupButtons() {
        val visibility = if (viewModel.isBackupEnabled() && viewModel.getBackupLocation() != null)
            View.VISIBLE
        else
            View.GONE

        binding.createBackup.visibility = visibility
        binding.autoBackupLocation.visibility = visibility
        binding.loadBackup.visibility = visibility
        updateBackupLocationText()
    }

    private fun observeBackupState() {
        lifecycleScope.launch {
            viewModel.backup.collect { success ->
                success?.let {
                    val message = if (it) {
                        getString(R.string.backup_created_successfully)
                    } else {
                        getString(R.string.backup_creation_failed)
                    }
                    Toast.makeText(this@SettingsActivity, message, Toast.LENGTH_LONG).show()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.loadBackup.collect {
                it?.let {
                    val message = if (it) {
                        getString(R.string.load_backup_successfully)
                    } else {
                        getString(R.string.load_backup_failed)
                    }
                    Toast.makeText(this@SettingsActivity, message, Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun checkAutofill() {
        binding.switchAutofill.isChecked = hasEnabledAutofill()
    }

    private fun checkBiometric() {
        if (viewModel.isBiometric()) {
            binding.switchBiometrics.isChecked = true
            binding.switchBiometrics.isEnabled = false
        } else {
            binding.switchBiometrics.isChecked = false
            binding.switchBiometrics.isEnabled = true
        }
    }

    private fun setListeners() {
        binding.btnChangeMasterPassword.setOnClickListener {
            ChangeMasterPasswordDialog().show(supportFragmentManager, "ChangeMasterPasswordDialog")
        }
        binding.switchBiometrics.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !viewModel.isBiometric()) {
                val intent = Intent(this, BiometricActivity::class.java)
                startActivity(intent)
            }
        }
        binding.btnExport.setOnClickListener {
            ExportPasswordDialog().show(supportFragmentManager, "ExportPasswordDialog")
        }
        binding.btnImport.setOnClickListener {
            ImportPasswordDialog().show(supportFragmentManager, "ImportPasswordDialog")
        }
        binding.btnClearPasswords.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.pref_title_clear_passwords))
                .setMessage(getString(R.string.clear_passwords_confirmation_message))
                .setPositiveButton(getString(R.string.delete)) { _, _ ->
                    viewModel.clearPasswords()
                    Toast.makeText(
                        this,
                        getString(R.string.passwords_clear_toast),
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }
        binding.autoBackup.setOnCheckedChangeListener { _, isChecked ->
            viewModel.enableBackup()
            checkBackupButtons()
            if (isChecked) {
                if (viewModel.getBackupLocation() == null) {
                    folderPicker.launch(FilePickerType.FOLDER)
                }
            } else {
                viewModel.disableBackup()
                checkBackupButtons()
            }
        }

        binding.autoBackupLocation.setOnClickListener {
            folderPicker.launch(FilePickerType.FOLDER)
        }

        binding.createBackup.setOnClickListener {
            viewModel.createBackup()
        }

        binding.loadBackup.setOnClickListener {
            filePicker.launch(FilePickerType.FILE, "application/octet-stream")
        }
        binding.switchAutofill.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                requestAutofillService()
            }
        }
    }

    private fun updateBackupLocationText() {
        val backupLocation = viewModel.getBackupLocation()
        if (backupLocation != null && viewModel.isBackupEnabled()) {
            val locationName =
                backupLocation.lastPathSegment?.split(':')?.last() ?: backupLocation.toString()
            val fullName = getString(R.string.backup_location) + locationName
            binding.backupLocationFile.text = fullName
            binding.backupLocationFile.visibility = View.VISIBLE
        } else {
            binding.backupLocationFile.visibility = View.GONE
        }
    }

    private fun requestAutofillService() {
        if (!hasEnabledAutofill()) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE)
                intent.data = "package:${packageName}".toUri()
                startActivity(intent)
                Toast.makeText(
                    this,
                    getString(R.string.please_select_autofill_service),
                    Toast.LENGTH_LONG
                ).show()
            } catch (_: ActivityNotFoundException) {
                val fallbackIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(fallbackIntent)
                Toast.makeText(
                    this,
                    getString(R.string.enable_autofill_in_settings),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun hasEnabledAutofill(): Boolean {
        return getSystemService(AutofillManager::class.java).hasEnabledAutofillServices()
    }
}
