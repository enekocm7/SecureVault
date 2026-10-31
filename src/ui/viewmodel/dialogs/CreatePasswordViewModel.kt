package ui.viewmodel.dialogs

import androidx.lifecycle.ViewModel
import domain.model.PasswordDto
import domain.usecases.password.AddPassword
import javax.inject.Inject

class CreatePasswordViewModel @Inject constructor(
    private val addPassword: AddPassword
) : ViewModel() {

    fun savePassword(passwordDto: PasswordDto) {
        addPassword(passwordDto.name,passwordDto)
    }
}