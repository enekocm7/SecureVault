package com.example.securevault.ui.viewmodel


import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securevault.domain.model.BiometricResult
import com.example.securevault.domain.usecases.auth.AuthenticateBiometrics
import com.example.securevault.domain.usecases.auth.GenerateBiometricKey
import com.example.securevault.domain.usecases.auth.GetEncryptCryptoObject
import com.example.securevault.ui.biometrics.BiometricPromptManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricViewModel
@Inject constructor(
    private val generateBiometricKey: GenerateBiometricKey,
    private val authenticateBiometrics: AuthenticateBiometrics,
    private val getEncryptCryptoObject: GetEncryptCryptoObject
) : ViewModel() {

    private val title = "Biometric Authentication"
    private val description = "Enable biometric authentication to secure your vault."

    private val _authenticationState = MutableLiveData<BiometricResult?>(null)
    val authenticationState: LiveData<BiometricResult?> = _authenticationState

    fun enableBiometric(activity: AppCompatActivity) {
        val biometricAuth = BiometricPromptManager(activity)
        viewModelScope.launch {
            biometricAuth.promptResults.collect { result ->
                _authenticationState.value = result
                generateKey(result)
            }
        }
        authenticateBiometrics(biometricAuth, title, description, getEncryptCryptoObject())
    }

    private fun generateKey(result: BiometricResult) {
        if (authenticationState.value is BiometricResult.AuthenticationSuccess) {
            viewModelScope.launch {
                generateBiometricKey(result)
            }
        }
    }

}