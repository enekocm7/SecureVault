package com.example.securevault.ui.activities

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.securevault.R
import com.example.securevault.ui.viewmodels.RegisterViewModel
import com.example.securevault.utils.PasswordStrength
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import androidx.core.graphics.toColorInt

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()
    private val label = "Strength:"
    private lateinit var masterPassword : TextInputEditText
    private lateinit var confirmPassword : TextInputEditText
    private lateinit var progressBarText : TextView
    private lateinit var progressBar : ProgressBar
    private lateinit var continueButton : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_master_password)

        masterPassword = findViewById(R.id.masterPasswordInput)
        progressBar = findViewById(R.id.passwordStrengthBar)
        progressBarText = findViewById(R.id.passwordStrengthLabel)
        confirmPassword = findViewById(R.id.confirmPasswordInput)
        continueButton = findViewById(R.id.continueButton)

        continueButton.setOnClickListener {

        }

        masterPassword.doAfterTextChanged {
            changeProgressBar()
        }
    }
    
    fun changeProgressBar(){
        val strength = viewModel.calculateStrength(masterPassword.text.toString())
        var text = label+" "+strength.label
        progressBarText.text = text
        progressBar.progress = strength.progress
        progressBar.progressTintList = ColorStateList.valueOf(strength.colorInt)
    }
}


