package com.enekocm.securevault.domain.usecases.password

import com.enekocm.securevault.domain.repository.PasswordRepository
import javax.inject.Inject

class DeleteAllPasswords @Inject constructor(private val passwordRepository: PasswordRepository) {
    operator fun invoke() = passwordRepository.deleteAllPasswords()
}