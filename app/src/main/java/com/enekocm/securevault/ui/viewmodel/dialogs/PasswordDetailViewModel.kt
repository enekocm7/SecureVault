package com.enekocm.securevault.ui.viewmodel.dialogs

import androidx.lifecycle.ViewModel
import com.enekocm.securevault.domain.model.PasswordDto
import com.enekocm.securevault.domain.usecases.password.AddPassword
import com.enekocm.securevault.domain.usecases.password.DeletePassword
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