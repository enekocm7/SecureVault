package com.enekocm.securevault.domain.usecases.password

import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.data.repository.factory.PasswordRepositoryFactory
import com.enekocm.securevault.domain.model.PasswordDto
import javax.inject.Inject

class AddPassword @Inject constructor(private val passwordRepositoryFactory: PasswordRepositoryFactory) {
    operator fun invoke(previousName:String, password: PasswordDto) = passwordRepositoryFactory.getPasswordRepository().insertPassword(previousName, PasswordMapper.mapToEntity(password))

    operator fun invoke(password: PasswordDto) = passwordRepositoryFactory.getPasswordRepository().insertPassword(PasswordMapper.mapToEntity(password))

}