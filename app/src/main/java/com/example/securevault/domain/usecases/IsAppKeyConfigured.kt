package com.example.securevault.domain.usecases

import com.example.securevault.domain.repository.MasterPasswordRepository
import javax.inject.Inject

class IsAppKeyConfigured @Inject constructor(private val repo: MasterPasswordRepository) {
    operator fun invoke(): Boolean {
        return repo.isAppKeyConfigured()
    }
}