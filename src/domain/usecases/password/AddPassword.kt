package domain.usecases.password

import data.mapper.PasswordMapper
import data.repository.factory.PasswordRepositoryFactory
import domain.model.PasswordDto
import javax.inject.Inject

class AddPassword @Inject constructor(private val passwordRepositoryFactory: PasswordRepositoryFactory) {
    operator fun invoke(previousName:String, password: PasswordDto) = passwordRepositoryFactory.getPasswordRepository().insertPassword(previousName, PasswordMapper.mapToEntity(password))

    operator fun invoke(password: PasswordDto) = passwordRepositoryFactory.getPasswordRepository().insertPassword(PasswordMapper.mapToEntity(password))

}