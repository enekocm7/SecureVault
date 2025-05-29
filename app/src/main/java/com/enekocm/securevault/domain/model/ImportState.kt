package com.enekocm.securevault.domain.model
sealed interface ImportState {
    object WrongPassword : ImportState
    object CsvFormatError : ImportState
    object Success: ImportState
}