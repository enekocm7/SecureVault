package domain.usecases.password

import data.json.crypto.FileEncryptor
import data.mapper.PasswordMapper
import domain.model.PasswordDto
import javax.inject.Inject

class GetPasswordsFromEncrypted @Inject constructor(private val fileEncryptor: FileEncryptor) {
    operator fun invoke(passwords: String, key: String): List<PasswordDto> = fileEncryptor.decryptPasswords(passwords,key).map { PasswordMapper.mapToDto(it) }
}