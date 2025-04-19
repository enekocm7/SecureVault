package com.example.securevault.ui.viewmodel


import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securevault.domain.model.BiometricResult
import com.example.securevault.domain.usecases.AuthenticateBiometrics
import com.example.securevault.domain.usecases.GenerateBiometricKey
import com.example.securevault.ui.biometrics.BiometricPromptManagerFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricViewModel
@Inject constructor(
    private val generateBiometricKey: GenerateBiometricKey,
    private val authenticateBiometrics: AuthenticateBiometrics,
    private val biometricPromptManagerFactory: BiometricPromptManagerFactory
) : ViewModel() {

    private val title = "Biometric Authentication"
    private val description = "Enable biometric authentication to secure your vault."

    private val _authenticationState = MutableStateFlow<BiometricResult?>(null)
    val authenticationState = _authenticationState.asStateFlow()

    fun enableBiometric(activity: AppCompatActivity) {
        val biometricAuth = biometricPromptManagerFactory.create(activity)
        viewModelScope.launch {
            biometricAuth.promptResults.collect {
                result -> _authenticationState.value = result
            }
        }
        authenticateBiometrics(biometricAuth,title,description)
        generateKey()
    }

    fun generateKey() {
        if (authenticationState.value is BiometricResult.AuthenticationSuccess){
            viewModelScope.launch {
                generateBiometricKey()
            }
        } else {
            _authenticationState.value = BiometricResult.AuthenticationNotRecognized
        }
    }

}