package ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import di.DispatcherProvider
import domain.model.PasswordDto
import domain.usecases.password.GetAllPasswords
import domain.usecases.password.GetPasswordsByName
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllPasswords: GetAllPasswords,
    private val getPasswordsByName: GetPasswordsByName,
    private val auth: FirebaseAuth?,
    private val dispatchers: DispatcherProvider
) :
    ViewModel() {

    private val _passwords = MutableStateFlow<List<PasswordDto>>(emptyList())
    val passwords: StateFlow<List<PasswordDto>> = _passwords.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPasswords()
    }

    fun loadPasswords(name: String) {
        viewModelScope.launch(dispatchers.io) {
            withContext(dispatchers.main) {
                _isLoading.value = true
            }
            val passwords = getPasswordsByNameIgnoreCase(name)
            withContext(dispatchers.main) {
                _passwords.value = passwords
                _isLoading.value = false
            }
        }
    }

    fun loadPasswords() {
        viewModelScope.launch(dispatchers.io) {
            withContext(dispatchers.main) {
                _isLoading.value = true
            }
            val passwords = getAllPasswords()
            withContext(dispatchers.main) {
                _passwords.value = passwords
                _isLoading.value = false
            }
        }
    }

    private fun getPasswordsByNameIgnoreCase(name: String): List<PasswordDto> {
        return getPasswordsByName(name)
    }

    fun isLoggedIn(): Boolean{
        return auth?.currentUser != null
    }

    fun profilePhotoUrl(): String? {
        return auth?.currentUser?.photoUrl?.toString()
    }
}
