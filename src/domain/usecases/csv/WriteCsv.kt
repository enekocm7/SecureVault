package domain.usecases.csv

import android.net.Uri
import data.csv.storage.CsvStorage
import data.mapper.PasswordMapper
import domain.model.PasswordDto
import javax.inject.Inject

class WriteCsv @Inject constructor(private val csvStorage: CsvStorage) {
    operator fun invoke(uri: Uri, passwords: List<PasswordDto>) =
        csvStorage.writeCsv(uri, passwords.map { PasswordMapper.mapToEntity(it) })
}