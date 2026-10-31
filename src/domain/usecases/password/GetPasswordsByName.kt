package domain.usecases.password

import data.mapper.PasswordMapper
import data.repository.factory.PasswordRepositoryFactory
import domain.model.PasswordDto
import javax.inject.Inject

class GetPasswordsByName @Inject constructor(private val passwordRepositoryFactory: PasswordRepositoryFactory) {
    operator fun invoke(name: String): List<PasswordDto>{
        return passwordRepositoryFactory.getPasswordRepository().getPasswordByNameContainingIgnoreCase(name).map { PasswordMapper.mapToDto(it) }
    }
}