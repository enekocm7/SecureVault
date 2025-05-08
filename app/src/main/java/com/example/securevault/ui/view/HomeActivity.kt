package com.example.securevault.ui.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.securevault.databinding.MainScreenBinding
import com.example.securevault.ui.view.fragments.CreatePasswordDialog
import com.example.securevault.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding : MainScreenBinding
    private val homeViewModel : HomeViewModel by viewModels()
    private val createPasswordDialog by lazy{
        CreatePasswordDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        binding = MainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners(){
        binding.addIcon.setOnClickListener{
            showCreatePasswordDialog()
        }
    }

    private fun showCreatePasswordDialog() {
        createPasswordDialog.show(this.supportFragmentManager, "Create new password")
    }

}