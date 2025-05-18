package com.example.securevault.ui.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.securevault.databinding.SettingsActivityBinding
import com.example.securevault.ui.view.fragments.ChangeMasterPasswordDialog
import com.example.securevault.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(){
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
    }

    private fun setListeners() {
        binding.btnChangeMasterPassword.setOnClickListener {
            val dialog = ChangeMasterPasswordDialog()
            dialog.show(supportFragmentManager, "ChangeMasterPasswordDialog")
        }
    }
}