package com.example.securevault.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securevault.domain.model.PasswordDto
import com.example.securevault.domain.usecases.password.AddPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavePasswordViewModel @Inject constructor(
    private val addPassword: AddPassword
) : ViewModel() {

    private val _saveState = MutableLiveData<SaveState>(SaveState.Initial)
    val saveState: LiveData<SaveState> = _saveState

    fun savePassword(name: String, url: String, username: String, password: String) {
        if (name.isBlank() || username.isBlank() || password.isBlank()) {
            _saveState.value = SaveState.Error("All fields are required")
            return
        }

        viewModelScope.launch {
            try {
                val passwordObject = PasswordDto(
                    name = name,
                    url = url,
                    username = username,
                    value = password
                )
                addPassword(passwordObject)
                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error("Failed to save password: ${e.message}")
            }
        }
    }

    sealed class SaveState {
        object Initial : SaveState()
        object Success : SaveState()
        data class Error(val message: String) : SaveState()
    }
}
