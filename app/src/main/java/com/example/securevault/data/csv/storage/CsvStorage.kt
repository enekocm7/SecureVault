package com.example.securevault.data.csv.storage

import android.content.Context
import android.net.Uri
import com.example.securevault.data.csv.formatter.CsvFormatter
import com.example.securevault.data.json.model.Password
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
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
        return try {
            context.contentResolver.openInputStream(uri)?.bufferedReader().use { reader ->
                val csvData = reader?.readText() ?: ""
                if (csvData.isBlank()) emptyList()
                else csvFormatter.unparsePasswordsWithHeader(csvData)
            }
        } catch (_: IOException) {
            emptyList()
        }
    }
}
