package com.example.securevault.ui.viewmodel.fragments

import androidx.lifecycle.ViewModel
import com.example.securevault.domain.usecases.generator.GeneratePassphrase
import com.example.securevault.domain.usecases.generator.GeneratePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GeneratePasswordViewModel @Inject constructor(
    private val generatePassword: GeneratePassword,
    private val generatePassphrase: GeneratePassphrase
) : ViewModel() {

    private val _password = MutableStateFlow<String>("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun getPassword(
        length: Int,
        lower: Boolean,
        upper: Boolean,
        numbers: Boolean,
        symbols: Boolean
    ) {
        _password.value = generatePassword(length, lower, upper, numbers, symbols)
    }

    fun getPassphrase(length: Int, delimiter: String) {
        _password.value = generatePassphrase(length, delimiter)
    }
}