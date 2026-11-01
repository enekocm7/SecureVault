package domain.usecases.sv

import android.content.Context
import android.net.Uri
import data.json.crypto.FileEncryptor
import data.mapper.PasswordMapper
import domain.model.PasswordDto
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReadSv @Inject constructor(
    private val fileEncryptor: FileEncryptor,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(uri: Uri, password: String): List<PasswordDto> =
        fileEncryptor.decryptPasswords(
            context.contentResolver.openInputStream(uri)?.bufferedReader()
                .use { it?.readText() ?: "" },
            password
        ).map { PasswordMapper.mapToDto(it) }
}