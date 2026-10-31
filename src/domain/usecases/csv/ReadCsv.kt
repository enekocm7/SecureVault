package domain.usecases.csv

import android.net.Uri
import data.csv.storage.CsvStorage
import data.mapper.PasswordMapper
import domain.model.PasswordDto
import javax.inject.Inject

class ReadCsv @Inject constructor(private val csvStorage: CsvStorage) {
    operator fun invoke(uri: Uri): List<PasswordDto> =
        csvStorage.readCsv(uri).map { PasswordMapper.mapToDto(it) }
}