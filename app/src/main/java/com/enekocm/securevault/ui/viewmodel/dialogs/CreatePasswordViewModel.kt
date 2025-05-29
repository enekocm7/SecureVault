package com.enekocm.securevault.ui.viewmodel.dialogs

import androidx.lifecycle.ViewModel
import com.enekocm.securevault.domain.model.PasswordDto
import com.enekocm.securevault.domain.usecases.password.AddPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePasswordViewModel @Inject constructor(
    private val addPassword: AddPassword
) : ViewModel() {

    fun savePassword(passwordDto: PasswordDto) {
        addPassword(passwordDto.name,passwordDto)
    }
}