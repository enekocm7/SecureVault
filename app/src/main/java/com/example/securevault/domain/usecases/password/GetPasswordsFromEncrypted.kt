package com.example.securevault.domain.usecases.password

import com.example.securevault.data.json.crypto.FileEncryptor
import com.example.securevault.data.mapper.PasswordMapper
import com.example.securevault.domain.model.PasswordDto
import javax.inject.Inject

class GetPasswordsFromEncrypted @Inject constructor(private val fileEncryptor: FileEncryptor) {
    operator fun invoke(passwords: String, key: String): List<PasswordDto> = fileEncryptor.decryptPasswords(passwords,key).map { PasswordMapper.mapToDto(it) }
}