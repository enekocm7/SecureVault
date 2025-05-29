package com.enekocm.securevault.data.csv.storage

import android.content.Context
import android.net.Uri
import com.enekocm.securevault.data.csv.formatter.CsvFormatter
import com.enekocm.securevault.data.json.model.Password
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CsvStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val csvFormatter: CsvFormatter
) {

    fun writeCsv(fileName: Uri, passwords: List<Password>) {
        val csvData = csvFormatter.parsePasswordsWithHeader(passwords)
        context.contentResolver.openOutputStream(fileName)?.bufferedWriter().use {
            it?.write(csvData)
        }
    }

    fun readCsv(uri: Uri): List<Password> {
        return context.contentResolver.openInputStream(uri)?.bufferedReader().use { reader ->
            val csvData = reader?.readText() ?: ""
            if (csvData.isBlank()) emptyList()
            else csvFormatter.unparsePasswordsWithHeader(csvData)
        }
    }

}
