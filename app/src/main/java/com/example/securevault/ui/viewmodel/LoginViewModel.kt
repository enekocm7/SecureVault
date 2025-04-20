package com.example.securevault.ui.viewmodel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securevault.domain.model.BiometricResult
import com.example.securevault.domain.usecases.AuthenticateBiometrics
import com.example.securevault.domain.usecases.GetDecryptCryptoObject
import com.example.securevault.domain.usecases.UnlockKeyWithBiometrics
import com.example.securevault.domain.usecases.UnlockKeyWithPassword
import com.example.securevault.ui.biometrics.BiometricPromptManagerFactory
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
    private val authenticateBiometrics: AuthenticateBiometrics,
    private val biometricPromptManagerFactory: BiometricPromptManagerFactory,
    private val getDecryptCryptoObject: GetDecryptCryptoObject
): ViewModel() {

    private val title = "Biometric Authentication"
    private val description = "Enable biometric authentication to secure your vault."

    private val _biometricLoginState = MutableStateFlow<Boolean?>(null)
    val biometricLoginState = _biometricLoginState.asStateFlow()

    fun login(password: String): Boolean{
        return unlockKeyWithPassword(password) != null
    }

    fun login(activity: AppCompatActivity){
        val biometricAuth = biometricPromptManagerFactory.create(activity)
        viewModelScope.launch {
            biometricAuth.promptResults.collect { result ->
                if (result is BiometricResult.AuthenticationSuccess) {
                    val appKey = unlockKeyWithBiometrics(result)
                    if (appKey != null) {
                        _biometricLoginState.value = true
                    } else {
                        _biometricLoginState.value = false
                    }
                } else if (result is BiometricResult.AuthenticationError ||
                    result is BiometricResult.AuthenticationFailed) {
                    _biometricLoginState.value = false
                }
            }
        }
        authenticateBiometrics(biometricAuth, title, description, getDecryptCryptoObject())
    }

}