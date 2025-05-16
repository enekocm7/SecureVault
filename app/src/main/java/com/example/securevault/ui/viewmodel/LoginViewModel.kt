package com.example.securevault.ui.viewmodel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securevault.domain.model.BiometricResult
import com.example.securevault.domain.usecases.auth.AuthenticateBiometrics
import com.example.securevault.domain.usecases.auth.GetDecryptCryptoObject
import com.example.securevault.domain.usecases.auth.IsBiometricConfigured
import com.example.securevault.domain.usecases.auth.UnlockKeyWithBiometrics
import com.example.securevault.domain.usecases.auth.UnlockKeyWithPassword
import com.example.securevault.ui.biometrics.BiometricPromptManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(
    private val unlockKeyWithPassword: UnlockKeyWithPassword,
    private val unlockKeyWithBiometrics: UnlockKeyWithBiometrics,
    private val isBiometricConfigured: IsBiometricConfigured,
    private val authenticateBiometrics: AuthenticateBiometrics,
    private val getDecryptCryptoObject: GetDecryptCryptoObject
) : ViewModel() {

    private val title = "Biometric Authentication"
    private val description = "Enable biometric authentication to secure your vault."

    private val _biometricLoginState = MutableStateFlow<Boolean?>(null)
    val biometricLoginState = _biometricLoginState.asStateFlow()

    private val _passwordLoginState = MutableStateFlow<Boolean?>(null)
    val passwordLoginState = _passwordLoginState.asStateFlow()

    fun login(password: String) {
        _passwordLoginState.value = unlockKeyWithPassword(password)
    }

    fun login(activity: AppCompatActivity) {
        val biometricAuth = BiometricPromptManager(activity)
        viewModelScope.launch {
            biometricAuth.promptResults.collect { result ->
                if (result is BiometricResult.AuthenticationSuccess) {
                    _biometricLoginState.value = unlockKeyWithBiometrics(result)
                } else if (result is BiometricResult.AuthenticationError || result is BiometricResult.AuthenticationFailed) {
                    _biometricLoginState.value = false
                }
            }
        }
        authenticateBiometrics(biometricAuth, title, description, getDecryptCryptoObject())
    }

    fun isBiometricKeyConfigured(): Boolean {
        return isBiometricConfigured()
    }

}