package com.enekocm.securevault.ui.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.enekocm.securevault.R
import com.enekocm.securevault.databinding.MainScreenBinding
import com.enekocm.securevault.ui.adapter.PasswordAdapter
import com.enekocm.securevault.ui.view.dialogs.CreatePasswordDialog
import com.enekocm.securevault.ui.viewmodel.HomeViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private var isSearchMode = false

    private lateinit var binding: MainScreenBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var passwordAdapter: PasswordAdapter
    private val auth = Firebase.auth

    companion object {
        const val PASSWORD_RELOAD_REQUEST_KEY = "passwordReload"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            @Suppress("DEPRECATION")
            window.statusBarColor = getColor(R.color.light_red)
        }

        binding = MainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeSettingsIcon()
        setupRecyclerView()
        setListeners()
        setObservers()
    }

    private fun changeSettingsIcon() {
        if (viewModel.isLoggedIn()) {
            auth.currentUser?.let { user ->
                user.photoUrl?.let {
                    Glide.with(this)
                        .load(it)
                        .into(binding.settingIcon)
                }
            }
        }else{
            binding.settingIcon.setImageResource(R.drawable.ic_settings)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPasswords()
        changeSettingsIcon()
    }

    private fun setObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.passwords.collect { passwordList ->
                    passwordAdapter.updatePasswords(passwordList.sortedBy {
                        it.name
                    })
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }

        supportFragmentManager.setFragmentResultListener(
            PASSWORD_RELOAD_REQUEST_KEY,
            this
        ) { requestKey, _ ->
            if (requestKey == PASSWORD_RELOAD_REQUEST_KEY) {
                viewModel.loadPasswords()
            }
        }

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.loadPasswords(text.toString())
        }
    }

    private fun setListeners() {
        binding.addIcon.setOnClickListener {
            CreatePasswordDialog(supportFragmentManager).show(
                supportFragmentManager,
                "Create new password"
            )
        }

        binding.search.setOnClickListener {
            isSearchMode = !isSearchMode
            if (isSearchMode) {
                enterSearchMode()
            } else {
                exitSearchMode()
            }
        }

        binding.settingIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        passwordAdapter = PasswordAdapter()
        val recyclerView = binding.rvPasswords
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = passwordAdapter
    }

    private fun enterSearchMode() {
        binding.settingIcon.visibility = View.GONE
        binding.title.visibility = View.GONE
        binding.searchEditText.visibility = View.VISIBLE
        binding.search.setImageResource(R.drawable.ic_close)
        binding.searchEditText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun exitSearchMode() {
        binding.searchEditText.visibility = View.GONE
        binding.settingIcon.visibility = View.VISIBLE
        binding.title.visibility = View.VISIBLE
        binding.search.setImageResource(R.drawable.ic_search)
        binding.searchEditText.text.clear()
    }
}
