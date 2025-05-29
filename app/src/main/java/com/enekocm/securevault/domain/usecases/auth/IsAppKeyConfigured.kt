package com.enekocm.securevault.domain.usecases.auth

import com.enekocm.securevault.domain.repository.MasterPasswordRepository
import javax.inject.Inject

class IsAppKeyConfigured @Inject constructor(private val repo: MasterPasswordRepository) {
    operator fun invoke(): Boolean {
        return repo.isAppKeyConfigured()
    }
}