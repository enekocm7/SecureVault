package com.example.securevault.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.securevault.domain.usecases.auth.IsBiometricConfigured
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val isBiometricConfigured: IsBiometricConfigured) :
    ViewModel() {

    fun isBiometric(): Boolean {
        return isBiometricConfigured()
    }
}