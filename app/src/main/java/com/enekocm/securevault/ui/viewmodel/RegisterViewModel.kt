package com.enekocm.securevault.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enekocm.securevault.domain.model.PasswordDto
import com.enekocm.securevault.domain.model.PasswordStrength
import com.enekocm.securevault.domain.usecases.EstimatePassword
import com.enekocm.securevault.domain.usecases.auth.GenerateAppKey
import com.enekocm.securevault.domain.usecases.auth.IsAppKeyConfigured
import com.enekocm.securevault.domain.usecases.password.AddPassword
import com.enekocm.securevault.domain.usecases.password.DeletePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val estimatePassword: EstimatePassword,
    private val generateAppKey: GenerateAppKey,
    private val addPassword: AddPassword,
    private val deletePassword: DeletePassword,
    private val isAppKeyConfigured: IsAppKeyConfigured,
) : ViewModel() {

    private val _passwordStrength = MutableLiveData<PasswordStrength>()
    val passwordStrength: LiveData<PasswordStrength> = _passwordStrength

    fun calculateStrength(password: String) {
        viewModelScope.launch {
            val strength = estimatePassword(password)
            _passwordStrength.value = strength
        }
    }

    fun createAppKey(password: String) {
        viewModelScope.launch {
            generateAppKey(password)
        }
    }

    fun isKeyConfigured(): Boolean {
        return isAppKeyConfigured()
    }

    fun reloadFirebaseCredentials() {
        val model = PasswordDto("a","a","a","a")
        addPassword(model)
        deletePassword(model)
    }
}
