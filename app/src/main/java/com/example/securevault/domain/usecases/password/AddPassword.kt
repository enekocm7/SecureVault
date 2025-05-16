package com.example.securevault.domain.usecases.password

import com.example.securevault.data.mapper.PasswordMapper
import com.example.securevault.domain.model.PasswordDto
import com.example.securevault.domain.repository.PasswordRepository
import javax.inject.Inject

class AddPassword @Inject constructor(private val passwordRepository: PasswordRepository) {
    operator fun invoke(password: PasswordDto) {
        passwordRepository.insertPassword(PasswordMapper.mapToEntity(password))
    }
}