package com.enekocm.securevault.domain.usecases.csv

import android.net.Uri
import com.enekocm.securevault.data.csv.storage.CsvStorage
import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.domain.model.PasswordDto
import javax.inject.Inject

class WriteCsv @Inject constructor(private val csvStorage: CsvStorage) {
    operator fun invoke(uri: Uri, passwords: List<PasswordDto>) =
        csvStorage.writeCsv(uri, passwords.map { PasswordMapper.mapToEntity(it) })
}