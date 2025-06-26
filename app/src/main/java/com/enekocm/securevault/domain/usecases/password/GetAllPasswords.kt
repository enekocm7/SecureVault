package com.enekocm.securevault.domain.usecases.password

import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.data.repository.factory.PasswordRepositoryFactory
import com.enekocm.securevault.domain.model.PasswordDto
import javax.inject.Inject

class GetAllPasswords @Inject constructor(private val passwordRepositoryFactory: PasswordRepositoryFactory) {
    operator fun invoke(): List<PasswordDto> {
        return passwordRepositoryFactory.getPasswordRepository().getAllPasswords()
            .map { password -> PasswordMapper.mapToDto(password) }.toList()
    }
}