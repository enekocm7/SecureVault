package domain.usecases.sv

import android.content.Context
import android.net.Uri
import data.json.crypto.FileEncryptor
import data.mapper.PasswordMapper
import domain.model.PasswordDto
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WriteSv @Inject constructor(
    private val encryptor: FileEncryptor,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(uri: Uri, passwords: List<PasswordDto>, password: String) {
        val encryptedPasswords =
            encryptor.encryptPasswords(passwords.map { PasswordMapper.mapToEntity(it) }, password)

        context.contentResolver.openOutputStream(uri)?.bufferedWriter().use {
            it?.write(encryptedPasswords)
        }
    }
}