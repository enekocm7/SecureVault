package domain.usecases.password

import data.mapper.PasswordMapper
import data.repository.factory.PasswordRepositoryFactory
import domain.model.PasswordDto
import javax.inject.Inject

class InsertAllPasswords @Inject constructor(private val passwordRepositoryFactory: PasswordRepositoryFactory) {
    operator fun invoke(passwords: List<PasswordDto>) =
        passwordRepositoryFactory.getPasswordRepository().insertAllPasswords(passwords.map { PasswordMapper.mapToEntity(it) })
}