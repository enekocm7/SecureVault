package com.enekocm.securevault.ui.viewmodel.dialogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enekocm.securevault.di.DispatcherProvider
import com.enekocm.securevault.domain.model.PasswordStrength
import com.enekocm.securevault.domain.usecases.EstimatePassword
import com.enekocm.securevault.domain.usecases.generator.GeneratePassphrase
import com.enekocm.securevault.domain.usecases.generator.GeneratePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GeneratePasswordViewModel @Inject constructor(
    private val generatePassword: GeneratePassword,
    private val generatePassphrase: GeneratePassphrase,
    private val estimatePassword: EstimatePassword,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _passwordStrength = MutableStateFlow(PasswordStrength.VERY_WEAK)
    val passwordStrength: StateFlow<PasswordStrength> = _passwordStrength.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    fun getPassword(
        length: Int,
        lower: Boolean,
        upper: Boolean,
        numbers: Boolean,
        symbols: Boolean
    ) {
        viewModelScope.launch(dispatcherProvider.default) {
            _isLoading.value = true
            val password = generatePassword(length, lower, upper, numbers, symbols)
            val strength = estimatePassword(password)
            withContext(dispatcherProvider.main) {
                _password.value = password
                _passwordStrength.value = strength
                _isLoading.value = false
            }
        }
    }

    fun getPassphrase(length: Int, delimiter: String) {
        viewModelScope.launch(dispatcherProvider.default) {
            _isLoading.value = true
            val passphrase = generatePassphrase(length, delimiter)
            val strength = estimatePassword(passphrase)
            withContext(dispatcherProvider.main) {
                _password.value = passphrase
                _passwordStrength.value = strength
                _isLoading.value = false
            }
        }
    }

}