package com.example.securevault.domain.usecases.auth

import com.example.securevault.domain.repository.MasterPasswordRepository
import javax.inject.Inject

class UnlockKeyWithPassword @Inject constructor(private val repo: MasterPasswordRepository) {
    operator fun invoke(password: String): Boolean = repo.unlockAppKeyWithPassword(password)
}