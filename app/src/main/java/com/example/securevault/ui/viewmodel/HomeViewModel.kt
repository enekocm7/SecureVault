package com.example.securevault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securevault.domain.model.PasswordDto
import com.example.securevault.domain.usecases.password.GetAllPasswords
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val getAllPasswords: GetAllPasswords) :
    ViewModel() {

    private val _passwords = MutableStateFlow<List<PasswordDto>>(emptyList())
    val passwords: StateFlow<List<PasswordDto>> = _passwords.asStateFlow()

    init {
        loadPasswords()
    }

    fun getPasswords(): List<PasswordDto> {
        return getAllPasswords()
    }

    fun loadPasswords() {
        viewModelScope.launch {
            _passwords.value = getPasswords()
        }
    }

}