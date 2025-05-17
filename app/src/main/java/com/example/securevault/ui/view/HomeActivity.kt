package com.example.securevault.ui.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securevault.databinding.MainScreenBinding
import com.example.securevault.ui.adapter.PasswordAdapter
import com.example.securevault.ui.view.fragments.CreatePasswordDialog
import com.example.securevault.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: MainScreenBinding
    private val viewModel: HomeViewModel by viewModels()
    private val createPasswordDialog by lazy {
        CreatePasswordDialog()
    }
    private lateinit var passwordAdapter: PasswordAdapter

    companion object{
        const val PASSWORD_RELOAD_REQUEST_KEY = "passwordReload"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true
        binding = MainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()

        setListeners()
        setObservers()
    }

    private fun setObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.passwords.collect { passwordList ->
                    passwordAdapter.updatePasswords(passwordList)
                }
            }
        }

        supportFragmentManager.setFragmentResultListener(PASSWORD_RELOAD_REQUEST_KEY, this) {
            requestKey, bundle ->
            if (requestKey == PASSWORD_RELOAD_REQUEST_KEY){
                viewModel.loadPasswords()
            }
        }

    }

    private fun setListeners() {
        binding.addIcon.setOnClickListener {
            createPasswordDialog.show(this.supportFragmentManager, "Create new password")
        }
    }

    private fun setupRecyclerView() {
        passwordAdapter = PasswordAdapter()
        val recyclerView = binding.rvPasswords
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = passwordAdapter
    }

}