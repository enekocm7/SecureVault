package com.example.securevault.ui.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.securevault.databinding.ActivitySavePasswordBinding
import com.example.securevault.ui.viewmodel.SavePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavePasswordActivity : AppCompatActivity() {

    private val viewModel: SavePasswordViewModel by viewModels()
    private lateinit var binding: ActivitySavePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        val url = intent.getStringExtra("url") ?: ""
        val username = intent.getStringExtra("username") ?: ""
        val password = intent.getStringExtra("password") ?: ""

        // Pre-fill the fields
        binding.textUrl.text = url
        binding.editUsername.setText(username)
        binding.editPassword.setText(password)

        // Default name from URL
        val defaultName = if (url.isNotEmpty()) {
            url.split(".").firstOrNull() ?: url
        } else {
            "Password"
        }
        binding.editName.setText(defaultName)

        // Setup buttons
        binding.buttonSave.setOnClickListener {
            viewModel.savePassword(
                name = binding.editName.text.toString(),
                url = url,
                username = binding.editUsername.text.toString(),
                password = binding.editPassword.text.toString()
            )
        }

        binding.buttonCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        viewModel.saveState.observe(this) { state ->
            when (state) {
                is SavePasswordViewModel.SaveState.Success -> {
                    Toast.makeText(this, "Password saved successfully", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is SavePasswordViewModel.SaveState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> { /* no-op */ }
            }
        }
    }

    companion object {
        const val REQUEST_SAVE_PASSWORD = 1001
    }
}
