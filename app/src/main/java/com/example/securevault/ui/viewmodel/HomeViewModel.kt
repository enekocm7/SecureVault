package com.example.securevault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securevault.domain.model.PasswordDto
import com.example.securevault.domain.usecases.password.GetAllPasswords
import com.example.securevault.domain.usecases.password.GetPasswordsByName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllPasswords: GetAllPasswords,
    private val getPasswordsByName: GetPasswordsByName
) :
    ViewModel() {

    private val _passwords = MutableStateFlow<List<PasswordDto>>(emptyList())
    val passwords: StateFlow<List<PasswordDto>> = _passwords.asStateFlow()

    init {
        loadPasswords()
    }

    private fun getPasswords(): List<PasswordDto> {
        return getAllPasswords()
    }


    fun loadPasswords(name: String){
        viewModelScope.launch(Dispatchers.IO) {
            val passwords = getPasswordsByNameIgnoreCase(name)
            withContext(Dispatchers.Main) {
                _passwords.value = passwords
            }
        }
    }

    fun loadPasswords() {
        viewModelScope.launch(Dispatchers.IO) {
            val passwords = getPasswords()
            withContext(Dispatchers.Main) {
                _passwords.value = passwords
            }
        }
    }


    private fun getPasswordsByNameIgnoreCase(name: String): List<PasswordDto>{
        return getPasswordsByName(name)
    }

}