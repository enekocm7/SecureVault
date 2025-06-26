package com.enekocm.securevault.domain.usecases.password

import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.data.repository.factory.PasswordRepositoryFactory
import com.enekocm.securevault.domain.model.PasswordDto
import javax.inject.Inject

class GetPasswordsByName @Inject constructor(private val passwordRepositoryFactory: PasswordRepositoryFactory) {
    operator fun invoke(name: String): List<PasswordDto>{
        return passwordRepositoryFactory.getPasswordRepository().getPasswordByNameContainingIgnoreCase(name).map { PasswordMapper.mapToDto(it) }
    }
}