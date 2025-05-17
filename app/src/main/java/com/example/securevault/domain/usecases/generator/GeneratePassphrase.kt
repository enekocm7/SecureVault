package com.example.securevault.domain.usecases.generator

import com.example.securevault.data.generator.PasswordGenerator
import javax.inject.Inject

class GeneratePassphrase @Inject constructor() {
    operator fun invoke(length: Int, delimiter: String): String {
        return PasswordGenerator.generatePassphrase(length, delimiter)
    }
}