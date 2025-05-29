package com.enekocm.securevault.domain.usecases.csv

import android.net.Uri
import com.enekocm.securevault.data.csv.storage.CsvStorage
import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.domain.model.PasswordDto
import javax.inject.Inject

class ReadCsv @Inject constructor(private val csvStorage: CsvStorage) {
    operator fun invoke(uri: Uri): List<PasswordDto> =
        csvStorage.readCsv(uri).map { PasswordMapper.mapToDto(it) }
}