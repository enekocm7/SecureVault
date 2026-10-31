package domain.usecases.password

import data.mapper.PasswordMapper
import data.repository.factory.PasswordRepositoryFactory
import domain.model.PasswordDto
import javax.inject.Inject

class GetAllPasswords @Inject constructor(private val passwordRepositoryFactory: PasswordRepositoryFactory) {
    operator fun invoke(): List<PasswordDto> {
        return passwordRepositoryFactory.getPasswordRepository().getAllPasswords()
            .map { password -> PasswordMapper.mapToDto(password) }.toList()
    }
}