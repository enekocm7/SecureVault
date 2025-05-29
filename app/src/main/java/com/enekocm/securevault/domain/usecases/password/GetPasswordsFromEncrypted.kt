package com.enekocm.securevault.domain.usecases.password

import com.enekocm.securevault.data.json.crypto.FileEncryptor
import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.domain.model.PasswordDto
import javax.inject.Inject

class GetPasswordsFromEncrypted @Inject constructor(private val fileEncryptor: FileEncryptor) {
    operator fun invoke(passwords: String, key: String): List<PasswordDto> = fileEncryptor.decryptPasswords(passwords,key).map { PasswordMapper.mapToDto(it) }
}