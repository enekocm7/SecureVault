package com.enekocm.securevault.domain.usecases.generator

import com.enekocm.securevault.data.generator.PasswordGenerator
import javax.inject.Inject

class GeneratePassword @Inject constructor() {
    operator fun invoke(
        length: Int,
        lower: Boolean,
        upper: Boolean,
        numbers: Boolean,
        symbols: Boolean
    ): String {
        return PasswordGenerator.generatePassword(length, lower, upper, numbers, symbols) ?: ""
    }
}