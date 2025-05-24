package com.example.securevault.domain.usecases.password

import com.example.securevault.domain.repository.PasswordRepository
import javax.inject.Inject

class DeleteAllPasswords @Inject constructor(private val passwordRepository: PasswordRepository) {
    operator fun invoke() = passwordRepository.deleteAllPasswords()
}