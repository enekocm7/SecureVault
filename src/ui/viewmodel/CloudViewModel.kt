package ui.viewmodel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import domain.model.AuthState
import utils.GoogleLogin
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CloudViewModel @Inject constructor(
    private val auth: FirebaseAuth?
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val currentUser = auth?.currentUser
        _authState.value = if (currentUser != null) {
            AuthState.Authenticated(currentUser)
        } else {
            AuthState.Unauthenticated
        }
    }

    suspend fun signInWithGoogle(activity: AppCompatActivity, allowNewAccounts: Boolean) {

            try {
                _isLoading.value = true
                _errorMessage.value = null
                val result = GoogleLogin(activity).signIn(allowNewAccounts)

                result?.user?.let { user ->
                    _authState.value = AuthState.Authenticated(user)
                } ?: run {
                    _errorMessage.value = "Failed to sign in with Google"
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown error occurred"
                _authState.value = AuthState.Unauthenticated
            } finally {
                _isLoading.value = false
            }

    }

    fun clearError() {
        _errorMessage.value = null
    }

}
