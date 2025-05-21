package com.example.securevault.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.securevault.R
import com.example.securevault.databinding.SettingsActivityBinding
import com.example.securevault.ui.view.dialogs.ChangeMasterPasswordDialog
import com.example.securevault.ui.view.dialogs.ExportPasswordDialog
import com.example.securevault.ui.view.dialogs.ImportPasswordDialog
import com.example.securevault.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingsActivityBinding
    private val viewModel: SettingsViewModel by viewModels()

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
    }
}


