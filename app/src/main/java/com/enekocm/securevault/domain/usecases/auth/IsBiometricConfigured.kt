package com.enekocm.securevault.domain.usecases.auth

import com.enekocm.securevault.domain.repository.MasterPasswordRepository
import javax.inject.Inject

class IsBiometricConfigured @Inject constructor(private val repo: MasterPasswordRepository) {
    operator fun invoke() = repo.isBiometricConfigured()
}