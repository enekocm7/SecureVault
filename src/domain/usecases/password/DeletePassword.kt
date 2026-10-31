package domain.usecases.password

import data.mapper.PasswordMapper
import data.repository.factory.PasswordRepositoryFactory
import domain.model.PasswordDto
import javax.inject.Inject

class DeletePassword @Inject constructor(private val passwordRepositoryFactory: PasswordRepositoryFactory) {
    operator fun invoke(passwordDto: PasswordDto) = passwordRepositoryFactory.getPasswordRepository().deletePassword(PasswordMapper.mapToEntity(passwordDto))

}