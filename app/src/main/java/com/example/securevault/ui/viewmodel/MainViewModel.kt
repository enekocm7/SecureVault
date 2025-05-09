package com.example.securevault.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.securevault.domain.usecases.auth.IsAppKeyConfigured
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isAppKeyConfigured: IsAppKeyConfigured
) : ViewModel() {

    fun isKeyConfigured(): Boolean {
        return isAppKeyConfigured()
    }

}