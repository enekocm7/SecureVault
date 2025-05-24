package com.example.securevault.domain.model
sealed interface ImportState {
    object WrongPassword : ImportState
    object CsvFormatError : ImportState
    object Success: ImportState
}