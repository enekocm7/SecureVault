package com.example.securevault.domain.usecases.auth

import com.example.securevault.domain.repository.MasterPasswordRepository
import javax.inject.Inject

class GenerateAppKey @Inject constructor(private val repo: MasterPasswordRepository) {
    suspend operator fun invoke(password: String) = repo.generateAndStoreAppKey(password)
}