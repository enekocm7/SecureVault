package com.example.securevault.domain.usecases

import com.example.securevault.domain.repository.MasterPasswordRepository

class UnlockKeyWithPassword(private val repo: MasterPasswordRepository) {
    suspend operator fun invoke(password: String): ByteArray? = repo.unlockAppKeyWithPassword(password)
}