package ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.PasswordDto
import domain.model.PasswordStrength
import domain.usecases.EstimatePassword
import domain.usecases.auth.GenerateAppKey
import domain.usecases.auth.IsAppKeyConfigured
import domain.usecases.password.AddPassword
import domain.usecases.password.DeletePassword
import kotlinx.coroutines.launch
import javax.inject.Inject

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
