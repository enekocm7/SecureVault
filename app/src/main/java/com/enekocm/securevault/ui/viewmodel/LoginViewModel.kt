package com.enekocm.securevault.ui.viewmodel

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enekocm.securevault.R
import com.enekocm.securevault.data.json.model.Password
import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.di.DispatcherProvider
import com.enekocm.securevault.domain.model.BiometricResult
import com.enekocm.securevault.domain.usecases.auth.AuthenticateBiometrics
import com.enekocm.securevault.domain.usecases.auth.GetDecryptCryptoObject
import com.enekocm.securevault.domain.usecases.auth.IsBiometricConfigured
import com.enekocm.securevault.domain.usecases.auth.UnlockKeyWithBiometrics
import com.enekocm.securevault.domain.usecases.firestore.UnlockKeyWithFirebase
import com.enekocm.securevault.domain.usecases.auth.UnlockKeyWithPassword
import com.enekocm.securevault.domain.usecases.firestore.GetModel
import com.enekocm.securevault.domain.usecases.firestore.ModelToPreferences
import com.enekocm.securevault.domain.usecases.password.GetAllPasswords
import com.enekocm.securevault.ui.biometrics.BiometricPromptManager
import com.enekocm.securevault.utils.GoogleLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context,
    private val unlockKeyWithPassword: UnlockKeyWithPassword,
    private val unlockKeyWithBiometrics: UnlockKeyWithBiometrics,
    private val unlockKeyWithFirebase: UnlockKeyWithFirebase,
    private val isBiometricConfigured: IsBiometricConfigured,
    private val authenticateBiometrics: AuthenticateBiometrics,
    private val getDecryptCryptoObject: GetDecryptCryptoObject,
    private val modelToPreferences: ModelToPreferences,
    private val getModel: GetModel,
    private val getAllPasswords: Lazy<GetAllPasswords>,
    private val dispatchers: DispatcherProvider,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val title = context.getString(R.string.biometric_authentication)
    private val description =
        context.getString(R.string.enable_biometric_authentication_to_secure_your_vault)

    private val _biometricLoginState = MutableStateFlow<Boolean?>(null)
    val biometricLoginState = _biometricLoginState.asStateFlow()

    private val _passwordLoginState = MutableStateFlow<Boolean?>(null)
    val passwordLoginState = _passwordLoginState.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _accountSwitchResult = MutableStateFlow<Boolean?>(null)
    val accountSwitchResult = _accountSwitchResult.asStateFlow()

    private val cryptoObjectDeferred by lazy {
        viewModelScope.async(dispatchers.default) {
            getDecryptCryptoObject()
        }
    }

    init {
        _currentUser.value = firebaseAuth.currentUser
    }

    fun login(password: String) {
        if (_currentUser.value==null) loginPassword(password) else loginFirebase(password)

    }

    private fun loginFirebase(password: String){
        viewModelScope.launch(dispatchers.default) {
            val model = getModel()?: run {
                _passwordLoginState.value = false
                return@launch
            }
            val result = unlockKeyWithFirebase(password, modelToPreferences(model))
            withContext(dispatchers.main) {
                _passwordLoginState.value = result
            }
        }
    }

    private fun loginPassword(password: String){
        viewModelScope.launch(dispatchers.default) {
            val result = unlockKeyWithPassword(password)
            withContext(dispatchers.main) {
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

    fun getPasswords(): List<Password> {
        return getAllPasswords.get()().map { PasswordMapper.mapToEntity(it) }
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    suspend fun switchToAccount(activity: AppCompatActivity): Boolean {
        return try {
            val googleLogin = GoogleLogin(activity)
            val result = googleLogin.signIn(allowNewAccounts = false)
            if (result != null) {
                _currentUser.value = firebaseAuth.currentUser
                _accountSwitchResult.value = true
                true
            } else {
                _accountSwitchResult.value = false
                false
            }
        } catch (_: Exception) {
            _accountSwitchResult.value = false
            false
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                firebaseAuth.signOut()
                _currentUser.value = null
            } catch (_: Exception) {
            }
        }
    }
}
