package com.example.securevault.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.securevault.utils.PasswordStrength
import com.example.securevault.utils.estimate


class RegisterViewModel : ViewModel() {

    fun calculateStrength(password: String): PasswordStrength{
        return estimate(password)
    }

}