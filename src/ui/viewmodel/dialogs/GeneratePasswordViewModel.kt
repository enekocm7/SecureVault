package ui.viewmodel.dialogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import di.DispatcherProvider
import domain.model.PasswordStrength
import domain.usecases.EstimatePassword
import domain.usecases.generator.GeneratePassphrase
import domain.usecases.generator.GeneratePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

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