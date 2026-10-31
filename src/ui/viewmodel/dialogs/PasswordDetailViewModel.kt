package ui.viewmodel.dialogs

import androidx.lifecycle.ViewModel
import domain.model.PasswordDto
import domain.usecases.password.AddPassword
import domain.usecases.password.DeletePassword
import javax.inject.Inject

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