package domain.usecases.password

import data.repository.factory.PasswordRepositoryFactory
import javax.inject.Inject

class DeleteAllPasswords @Inject constructor(private val passwordRepositoryFactory: PasswordRepositoryFactory) {
    operator fun invoke() = passwordRepositoryFactory.getPasswordRepository().deleteAllPasswords()
}