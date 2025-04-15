package com.example.securevault.domain.usecases

import com.example.securevault.domain.repository.MasterPasswordRepository

class GenerateAppKey(private val repo: MasterPasswordRepository) {
    suspend operator fun invoke(password: String) = repo.generateAndStoreAppKey(password)
}