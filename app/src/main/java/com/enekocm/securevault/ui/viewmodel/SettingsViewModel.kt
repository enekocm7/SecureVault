package com.enekocm.securevault.ui.viewmodel

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.di.DispatcherProvider
import com.enekocm.securevault.domain.backup.BackupManager
import com.enekocm.securevault.domain.model.AuthState
import com.enekocm.securevault.domain.usecases.auth.IsBiometricConfigured
import com.enekocm.securevault.domain.usecases.password.DeleteAllPasswords
import com.enekocm.securevault.domain.usecases.password.GetAllPasswords
import com.enekocm.securevault.utils.GoogleLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val isBiometricConfigured: IsBiometricConfigured,
    private val deleteAllPasswords: DeleteAllPasswords,
    private val backupManager: BackupManager,
    private val getAllPasswords: GetAllPasswords,
    private val dispatchers: DispatcherProvider
) :
    ViewModel() {

    private val _backup = MutableSharedFlow<Boolean>()
    val backup = _backup.asSharedFlow()

    private val _loadBackup = MutableSharedFlow<Boolean>()
    val loadBackup = _loadBackup.asSharedFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun isBiometric(): Boolean {
        return isBiometricConfigured()
    }

    fun clearPasswords() {
        deleteAllPasswords()
    }

    fun loadBackup(uri: Uri) {
        viewModelScope.launch {
            try {
                backupManager.loadBackup(uri)
                _loadBackup.emit(true)
            } catch (_: Exception) {
                _loadBackup.emit(false)
            }
        }
    }

    fun createBackup() {
        viewModelScope.launch(dispatchers.io) {
            val passwords = getAllPasswords().map { PasswordMapper.mapToEntity(it) }
            _backup.emit(backupManager.createBackupIfEnabled(passwords))
        }
    }

    fun isBackupEnabled(): Boolean {
        return backupManager.isBackupEnabled()
    }

    fun enableBackup() {
        backupManager.enableBackup()
    }

    fun disableBackup() {
        backupManager.disableBackup()
    }

    fun getBackupLocation(): Uri? {
        return backupManager.getBackupLocation()
    }

    fun setBackupLocation(uri: Uri) {
        backupManager.setBackupLocation(uri)
    }

    fun signInWithGoogle(activity: AppCompatActivity) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
                val result = GoogleLogin(activity).signIn(allowNewAccounts = true)

                result?.user?.let { user ->
                    _authState.value = AuthState.Authenticated(user)
                } ?: run {
                    _errorMessage.value = "Failed to sign in with Google"
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown error occurred"
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

}

