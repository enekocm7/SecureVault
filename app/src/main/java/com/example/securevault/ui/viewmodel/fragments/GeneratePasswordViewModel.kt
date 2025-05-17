package com.example.securevault.ui.viewmodel.fragments

import androidx.lifecycle.ViewModel
import com.example.securevault.domain.model.PasswordStrength
import com.example.securevault.domain.usecases.EstimatePassword
import com.example.securevault.domain.usecases.generator.GeneratePassphrase
import com.example.securevault.domain.usecases.generator.GeneratePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GeneratePasswordViewModel @Inject constructor(
    private val generatePassword: GeneratePassword,
    private val generatePassphrase: GeneratePassphrase,
    private val estimatePassword: EstimatePassword
) : ViewModel() {

    private val _password = MutableStateFlow<String>("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _passwordStrength = MutableStateFlow<PasswordStrength>(PasswordStrength.VERY_WEAK)
    val passwordStrength: StateFlow<PasswordStrength> = _passwordStrength.asStateFlow()

    fun getPassword(length: Int, lower: Boolean, upper: Boolean, numbers: Boolean, symbols: Boolean) {
        val password = generatePassword(length, lower, upper, numbers, symbols)
        _password.value = password
        _passwordStrength.value = estimatePassword(password)
    }

    fun getPassphrase(length: Int, delimiter: String) {
        val passphrase = generatePassphrase(length, delimiter)
        _password.value = passphrase
        _passwordStrength.value = estimatePassword(passphrase)
    }

}