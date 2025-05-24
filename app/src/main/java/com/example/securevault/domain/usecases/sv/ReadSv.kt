package com.example.securevault.domain.usecases.sv

import android.content.Context
import android.net.Uri
import com.example.securevault.data.json.crypto.FileEncryptor
import com.example.securevault.data.mapper.PasswordMapper
import com.example.securevault.domain.model.PasswordDto
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