package domain.usecases.sv

import android.content.Context
import android.net.Uri
import data.json.crypto.FileEncryptor
import data.mapper.PasswordMapper
import domain.model.PasswordDto
import javax.inject.Inject

class ReadSv @Inject constructor(
    private val fileEncryptor: FileEncryptor,
    private val context: Context
) {
    operator fun invoke(uri: Uri, password: String): List<PasswordDto> =
        fileEncryptor.decryptPasswords(
            context.contentResolver.openInputStream(uri)?.bufferedReader()
                .use { it?.readText() ?: "" },
            password
        ).map { PasswordMapper.mapToDto(it) }
}