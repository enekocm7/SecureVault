package com.enekocm.securevault.ui.viewmodel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enekocm.securevault.di.DispatcherProvider
import com.enekocm.securevault.domain.model.BiometricResult
import com.enekocm.securevault.domain.usecases.auth.AuthenticateBiometrics
import com.enekocm.securevault.domain.usecases.auth.GetDecryptCryptoObject
import com.enekocm.securevault.domain.usecases.auth.IsBiometricConfigured
import com.enekocm.securevault.domain.usecases.auth.UnlockKeyWithBiometrics
import com.enekocm.securevault.domain.usecases.auth.UnlockKeyWithPassword
import com.enekocm.securevault.ui.biometrics.BiometricPromptManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(
    private val unlockKeyWithPassword: UnlockKeyWithPassword,
    private val unlockKeyWithBiometrics: UnlockKeyWithBiometrics,
    private val isBiometricConfigured: IsBiometricConfigured,
    private val authenticateBiometrics: AuthenticateBiometrics,
    private val getDecryptCryptoObject: GetDecryptCryptoObject,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val title = "Biometric Authentication"
    private val description = "Enable biometric authentication to secure your vault."

    private val _biometricLoginState = MutableStateFlow<Boolean?>(null)
    val biometricLoginState = _biometricLoginState.asStateFlow()

    private val _passwordLoginState = MutableStateFlow<Boolean?>(null)
    val passwordLoginState = _passwordLoginState.asStateFlow()

    private val cryptoObjectDeferred by lazy {
        viewModelScope.async(dispatchers.default) {
            getDecryptCryptoObject()
        }
    }

    fun login(password: String) {
        viewModelScope.launch(dispatchers.default) {
            val result = unlockKeyWithPassword(password)
            withContext(dispatchers.main){
                _passwordLoginState.value = result
            }
        }
    }

    fun login(activity: AppCompatActivity) {
        val biometricAuth = BiometricPromptManager(activity)

        viewModelScope.launch {
            try {
                biometricAuth.promptResults.collect { result ->
                    when (result) {
                        is BiometricResult.AuthenticationSuccess -> {
                            val authResult: Boolean = unlockKeyWithBiometrics(result)
                            _biometricLoginState.value = authResult
                        }

                        is BiometricResult.AuthenticationError,
                        is BiometricResult.AuthenticationFailed,
                        is BiometricResult.AuthenticationNotRecognized,
                        is BiometricResult.FeatureUnavailable,
                        is BiometricResult.HardwareNotAvailable -> {
                            _biometricLoginState.value = false
                        }
                    }
                }
            } catch (_: CancellationException) {
                _biometricLoginState.value = false
            }
        }
        viewModelScope.launch {
            try {
                val cryptoObject = cryptoObjectDeferred.await()
                authenticateBiometrics(biometricAuth, title, description, cryptoObject)
            } catch (_: Exception) {
                _biometricLoginState.value = false
            }
        }
    }

    fun isBiometricKeyConfigured(): Boolean {
        return isBiometricConfigured()
    }

}