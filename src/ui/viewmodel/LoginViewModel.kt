package ui.viewmodel

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enekocm.securevault.R
import data.json.model.Password
import data.mapper.PasswordMapper
import di.DispatcherProvider
import domain.model.BiometricResult
import domain.usecases.auth.AuthenticateBiometrics
import domain.usecases.auth.GetDecryptCryptoObject
import domain.usecases.auth.IsBiometricConfigured
import domain.usecases.auth.UnlockKeyWithBiometrics
import domain.usecases.firestore.UnlockKeyWithFirebase
import domain.usecases.auth.UnlockKeyWithPassword
import domain.usecases.firestore.GetModel
import domain.usecases.firestore.ModelToPreferences
import domain.usecases.password.GetAllPasswords
import ui.biometrics.BiometricPromptManager
import utils.GoogleLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginViewModel
@Inject constructor(
    private val context: Context,
    private val unlockKeyWithPassword: UnlockKeyWithPassword,
    private val unlockKeyWithBiometrics: UnlockKeyWithBiometrics,
    private val unlockKeyWithFirebase: UnlockKeyWithFirebase,
    private val isBiometricConfigured: IsBiometricConfigured,
    private val authenticateBiometrics: AuthenticateBiometrics,
    private val getDecryptCryptoObject: GetDecryptCryptoObject,
    private val modelToPreferences: ModelToPreferences,
    private val getModel: GetModel,
    private val getAllPasswords: GetAllPasswords,
    private val dispatchers: DispatcherProvider,
    private val firebaseAuth: FirebaseAuth?
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
        _currentUser.value = firebaseAuth?.currentUser
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
            val prefs = modelToPreferences(model) ?: run { withContext(dispatchers.main) { _passwordLoginState.value = false }; return@launch }; val result = unlockKeyWithFirebase(password, prefs)
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
        return getAllPasswords().map { PasswordMapper.mapToEntity(it) }
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth?.currentUser
    }

    suspend fun switchToAccount(activity: AppCompatActivity): Boolean {
        return try {
            val googleLogin = GoogleLogin(activity)
            val result = googleLogin.signIn(allowNewAccounts = false)
            if (result != null) {
                _currentUser.value = firebaseAuth?.currentUser
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
                firebaseAuth?.signOut()
                _currentUser.value = null
            } catch (_: Exception) {
            }
        }
    }
}
