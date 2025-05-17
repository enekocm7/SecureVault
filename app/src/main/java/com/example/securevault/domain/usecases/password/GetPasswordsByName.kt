package com.example.securevault.domain.usecases.password

import com.example.securevault.data.mapper.PasswordMapper
import com.example.securevault.domain.model.PasswordDto
import com.example.securevault.domain.repository.PasswordRepository
import javax.inject.Inject

class GetPasswordsByName @Inject constructor(private val passwordRepository: PasswordRepository) {
    operator fun invoke(name: String): List<PasswordDto>{
        return passwordRepository.getPasswordByNameContainingIgnoreCase(name).map { PasswordMapper.mapToDto(it) }
    }
}