package com.example.securevault.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securevault.domain.entities.PasswordStrength
import com.example.securevault.domain.usecases.EstimatePassword
import kotlinx.coroutines.launch


class RegisterViewModel : ViewModel() {

    val passwordStrength = MutableLiveData<PasswordStrength>()
    val estimatePassword = EstimatePassword()

    fun calculateStrength(password: String){
        viewModelScope.launch {
           val strength = estimatePassword(password)
            passwordStrength.postValue(strength)
        }
    }

}