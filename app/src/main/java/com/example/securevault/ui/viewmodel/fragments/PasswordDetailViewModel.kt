package com.example.securevault.ui.viewmodel.fragments

import androidx.lifecycle.ViewModel
import com.example.securevault.domain.model.PasswordDto
import com.example.securevault.domain.usecases.password.AddPassword
import com.example.securevault.domain.usecases.password.DeletePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordDetailViewModel @Inject constructor(
    private val addPassword: AddPassword,
    private val deletePassword: DeletePassword
) :
    ViewModel() {

    fun savePassword(password: PasswordDto) {
        addPassword(password)
    }

    fun savePassword(previousName: String, password: PasswordDto){
        addPassword(previousName,password)
    }

    fun removePassword(password: PasswordDto) {
        deletePassword(password)
    }
}