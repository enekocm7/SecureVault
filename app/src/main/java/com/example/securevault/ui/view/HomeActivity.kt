package com.example.securevault.ui.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.securevault.databinding.MainScreenBinding
import com.example.securevault.ui.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding : MainScreenBinding
    private val homeViewModel : HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        binding = MainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}