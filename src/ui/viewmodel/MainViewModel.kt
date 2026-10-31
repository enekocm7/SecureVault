package ui.viewmodel

import androidx.lifecycle.ViewModel
import domain.usecases.auth.IsAppKeyConfigured
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val isAppKeyConfigured: IsAppKeyConfigured
) : ViewModel() {

    fun isKeyConfigured(): Boolean {
        return isAppKeyConfigured()
    }
}
