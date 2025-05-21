package com.example.securevault.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.securevault.domain.usecases.auth.IsBiometricConfigured
import com.example.securevault.domain.usecases.password.DeleteAllPasswords
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val isBiometricConfigured: IsBiometricConfigured,
    private val deleteAllPasswords: DeleteAllPasswords
) :
    ViewModel() {

    fun isBiometric(): Boolean {
        return isBiometricConfigured()
    }

    fun clearPasswords(){
        deleteAllPasswords()
    }
}