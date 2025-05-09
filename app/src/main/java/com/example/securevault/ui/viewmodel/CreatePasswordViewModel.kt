package com.example.securevault.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.securevault.data.json.model.Password
import com.example.securevault.data.mapper.PasswordMapper
import com.example.securevault.domain.model.PasswordDto
import com.example.securevault.domain.usecases.password.AddPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePasswordViewModel @Inject constructor(
    private val addPassword: AddPassword
) : ViewModel() {

    fun savePassword(passwordDto: PasswordDto) {
        val password: Password = PasswordMapper.mapToEntity(passwordDto)
        addPassword(password)
    }
}
