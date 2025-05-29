package com.enekocm.securevault.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.di.DispatcherProvider
import com.enekocm.securevault.domain.backup.BackupManager
import com.enekocm.securevault.domain.usecases.auth.IsBiometricConfigured
import com.enekocm.securevault.domain.usecases.password.DeleteAllPasswords
import com.enekocm.securevault.domain.usecases.password.GetAllPasswords
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _backup = MutableStateFlow<Boolean?>(null)
    val backup = _backup.asStateFlow()

    private val _loadBackup = MutableStateFlow<Boolean?>(null)
    val loadBackup = _loadBackup.asStateFlow()

    fun isBiometric(): Boolean {
        return isBiometricConfigured()
    }

    fun clearPasswords() {
        deleteAllPasswords()
    }

    fun loadBackup(uri: Uri){
        viewModelScope.launch{
            try {
                backupManager.loadBackup(uri)
                _loadBackup.value = true
            }catch (_: Exception){
                _loadBackup.value = false
            }
        }
    }

    fun createBackup() {
        viewModelScope.launch(dispatchers.io) {
            val passwords = getAllPasswords().map { PasswordMapper.mapToEntity(it) }
            _backup.value = backupManager.createBackupIfEnabled(passwords)
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
}
