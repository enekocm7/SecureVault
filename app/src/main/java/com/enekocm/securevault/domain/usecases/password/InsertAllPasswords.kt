package com.enekocm.securevault.domain.usecases.password

import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.data.repository.factory.PasswordRepositoryFactory
import com.enekocm.securevault.domain.model.PasswordDto
import javax.inject.Inject

class InsertAllPasswords @Inject constructor(private val passwordRepositoryFactory: PasswordRepositoryFactory) {
    operator fun invoke(passwords: List<PasswordDto>) =
        passwordRepositoryFactory.getPasswordRepository().insertAllPasswords(passwords.map { PasswordMapper.mapToEntity(it) })
}