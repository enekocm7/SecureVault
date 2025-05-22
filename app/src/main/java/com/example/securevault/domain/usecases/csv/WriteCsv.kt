package com.example.securevault.domain.usecases.csv

import android.net.Uri
import com.example.securevault.data.csv.storage.CsvStorage
import com.example.securevault.data.mapper.PasswordMapper
import com.example.securevault.domain.model.PasswordDto
import javax.inject.Inject

class WriteCsv @Inject constructor(private val csvStorage: CsvStorage) {
    operator fun invoke(uri: Uri, passwords: List<PasswordDto>) =
        csvStorage.writeCsv(uri.path ?: "", passwords.map { PasswordMapper.mapToEntity(it) })
}