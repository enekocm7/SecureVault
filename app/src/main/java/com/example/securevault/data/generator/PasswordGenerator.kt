package com.example.securevault.data.generator

import me.gosimple.nbvcxz.resources.Generator
import java.security.SecureRandom

object PasswordGenerator {

    const val LOWER = "abcdefghijklmnopqrstuvwxyz"
    const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    const val NUMBERS = "0123456789"
    const val SYMBOL = "@#!$%^&*(){}[]=+-_,.<>?/:;"


    fun generatePassword(
        length: Int = 16,
        lower: Boolean = true,
        upper: Boolean = true,
        numbers: Boolean = true,
        symbols: Boolean = true
    ): String? {
        var characters = ""
        if (lower) characters += LOWER
        if (upper) characters += UPPER
        if (numbers) characters += NUMBERS
        if (symbols) characters += SYMBOL

        if (characters.isEmpty()) return null

        val secureRandom = SecureRandom()
        return (0 until length).map {
            characters[secureRandom.nextInt(characters.length)]
        }.joinToString("")
    }

    fun generatePassphrase(length: Int = 5, delimiter: String): String {
        return Generator.generatePassphrase(delimiter, length)
    }

}