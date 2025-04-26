package com.example.securevault.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securevault.domain.model.PasswordStrength
import com.example.securevault.domain.usecases.EstimatePassword
import com.example.securevault.domain.usecases.GenerateAppKey
import com.example.securevault.domain.usecases.IsAppKeyConfigured
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val estimatePassword: EstimatePassword,
    private val generateAppKey: GenerateAppKey,
    private val isAppKeyConfigured: IsAppKeyConfigured,
) : ViewModel() {

    private val _passwordStrength = MutableLiveData<PasswordStrength>()
    val passwordStrength : LiveData<PasswordStrength> = _passwordStrength

    fun calculateStrength(password: String){
        viewModelScope.launch {
           val strength = estimatePassword(password)
            _passwordStrength.value = strength
        }
    }

    fun createAppKey(password: String){
        generateAppKey(password)
    }

     fun isKeyConfigured(): Boolean {
        return isAppKeyConfigured()

    }



}