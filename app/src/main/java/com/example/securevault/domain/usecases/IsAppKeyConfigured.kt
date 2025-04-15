package com.example.securevault.domain.usecases

import com.example.securevault.domain.repository.MasterPasswordRepository

class IsAppKeyConfigured(private val repo : MasterPasswordRepository) {
    suspend operator fun invoke(): Boolean = repo.isAppKeyConfigured()
}