package com.example.securevault.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.securevault.domain.model.PasswordDto
import com.example.securevault.domain.usecases.password.GetAllPasswords
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val getAllPasswords: GetAllPasswords) :
    ViewModel() {

    fun getPasswords(): List<PasswordDto> {
        return getAllPasswords()
    }

}