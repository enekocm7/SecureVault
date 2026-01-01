package com.enekocm.securevault.domain.usecases.sv

import android.content.Context
import android.net.Uri
import com.enekocm.securevault.data.json.crypto.FileEncryptor
import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.domain.model.PasswordDto
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WriteSv @Inject constructor(
    private val encryptor: FileEncryptor,
    @param:ApplicationContext private val context: Context
) {
    operator fun invoke(uri: Uri, passwords: List<PasswordDto>, password: String) {
        val encryptedPasswords =
            encryptor.encryptPasswords(passwords.map { PasswordMapper.mapToEntity(it) }, password)

        context.contentResolver.openOutputStream(uri)?.bufferedWriter().use {
            it?.write(encryptedPasswords)
        }
    }
}