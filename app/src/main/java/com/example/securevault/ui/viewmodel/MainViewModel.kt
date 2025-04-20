package com.example.securevault.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.securevault.domain.usecases.IsBiometricConfigured
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isAppKeyConfigured: IsBiometricConfigured
) : ViewModel() {

    fun isKeyConfigured(): Boolean {
        return isAppKeyConfigured()
    }

}