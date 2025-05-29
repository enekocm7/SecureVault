package com.enekocm.securevault.domain.usecases.auth

import com.enekocm.securevault.domain.repository.MasterPasswordRepository
import javax.inject.Inject

class UnlockKeyWithPassword @Inject constructor(private val repo: MasterPasswordRepository) {
    suspend operator fun invoke(password: String): Boolean = repo.unlockAppKeyWithPassword(password)
}