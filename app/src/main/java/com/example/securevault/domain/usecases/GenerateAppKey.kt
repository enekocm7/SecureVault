package com.example.securevault.domain.usecases

import com.example.securevault.domain.repository.MasterPasswordRepository
import javax.inject.Inject

class GenerateAppKey @Inject constructor(private val repo: MasterPasswordRepository) {
    operator fun invoke(password: String) = repo.generateAndStoreAppKey(password)
}