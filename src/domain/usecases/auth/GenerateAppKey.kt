package domain.usecases.auth

import domain.repository.MasterPasswordRepository
import javax.inject.Inject

class GenerateAppKey @Inject constructor(private val repo: MasterPasswordRepository) {
    suspend operator fun invoke(password: String) = repo.generateAndStoreAppKey(password)
}