package com.example.securevault.domain.usecases

import com.example.securevault.domain.repository.MasterPasswordRepository

class UnlockKeyWithBiometrics(private val repo: MasterPasswordRepository) {
    suspend operator fun invoke(): ByteArray? = repo.unlockAppKeyWithBiometrics()

}