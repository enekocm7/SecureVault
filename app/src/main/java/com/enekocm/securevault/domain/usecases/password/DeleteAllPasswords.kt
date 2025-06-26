package com.enekocm.securevault.domain.usecases.password

import com.enekocm.securevault.data.repository.factory.PasswordRepositoryFactory
import javax.inject.Inject

class DeleteAllPasswords @Inject constructor(private val passwordRepositoryFactory: PasswordRepositoryFactory) {
    operator fun invoke() = passwordRepositoryFactory.getPasswordRepository().deleteAllPasswords()
}