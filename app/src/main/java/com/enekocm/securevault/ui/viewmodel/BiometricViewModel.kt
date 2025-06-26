package com.enekocm.securevault.ui.viewmodel


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enekocm.securevault.R
import com.enekocm.securevault.domain.model.BiometricResult
import com.enekocm.securevault.domain.usecases.auth.AuthenticateBiometrics
import com.enekocm.securevault.domain.usecases.auth.GenerateBiometricKey
import com.enekocm.securevault.domain.usecases.auth.GetEncryptCryptoObject
import com.enekocm.securevault.ui.biometrics.BiometricPromptManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    private val generateBiometricKey: GenerateBiometricKey,
    private val authenticateBiometrics: AuthenticateBiometrics,
    private val getEncryptCryptoObject: GetEncryptCryptoObject
) : ViewModel() {

    private val title = context.getString(R.string.biometric_authentication)
    private val description = context.getString(R.string.enable_biometric_authentication_to_secure_your_vault)

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
