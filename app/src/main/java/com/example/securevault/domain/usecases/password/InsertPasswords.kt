package com.example.securevault.domain.usecases.password

import com.example.securevault.data.mapper.PasswordMapper
import com.example.securevault.domain.model.PasswordDto
import com.example.securevault.domain.repository.PasswordRepository
import javax.inject.Inject

class InsertPasswords @Inject constructor(private val passwordRepository: PasswordRepository) {
    operator fun invoke(passwords: List<PasswordDto>) = passwords.forEach { passwordDto ->
        passwordRepository.insertPassword(PasswordMapper.mapToEntity(passwordDto))
    }
}