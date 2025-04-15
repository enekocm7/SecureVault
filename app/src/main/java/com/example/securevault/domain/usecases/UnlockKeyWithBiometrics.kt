package com.example.securevault.domain.usecases

import com.example.securevault.domain.repository.MasterPasswordRepository
import javax.inject.Inject

class UnlockKeyWithBiometrics @Inject constructor(private val repo: MasterPasswordRepository) {
    operator fun invoke(): ByteArray? = repo.unlockAppKeyWithBiometrics()

}