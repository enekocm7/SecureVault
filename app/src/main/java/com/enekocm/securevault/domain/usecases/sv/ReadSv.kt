package com.enekocm.securevault.domain.usecases.sv

import android.content.Context
import android.net.Uri
import com.enekocm.securevault.data.json.crypto.FileEncryptor
import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.domain.model.PasswordDto
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReadSv @Inject constructor(
    private val fileEncryptor: FileEncryptor,
    @param:ApplicationContext private val context: Context
) {
    operator fun invoke(uri: Uri, password: String): List<PasswordDto> =
        fileEncryptor.decryptPasswords(
            context.contentResolver.openInputStream(uri)?.bufferedReader()
                .use { it?.readText() ?: "" },
            password
        ).map { PasswordMapper.mapToDto(it) }
}