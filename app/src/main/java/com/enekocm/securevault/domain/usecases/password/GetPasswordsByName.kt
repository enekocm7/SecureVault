package com.enekocm.securevault.domain.usecases.password

import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.domain.model.PasswordDto
import com.enekocm.securevault.domain.repository.PasswordRepository
import javax.inject.Inject

class GetPasswordsByName @Inject constructor(private val passwordRepository: PasswordRepository) {
    operator fun invoke(name: String): List<PasswordDto>{
        return passwordRepository.getPasswordByNameContainingIgnoreCase(name).map { PasswordMapper.mapToDto(it) }
    }
}