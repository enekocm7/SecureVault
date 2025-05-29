package com.enekocm.securevault.data.csv.formatter

import com.enekocm.securevault.data.json.model.Password
import javax.inject.Inject

class CsvFormatter @Inject constructor() {
    companion object {
        const val SEPARATOR = ','
        val HEADER = buildString {
            append("name")
            append(SEPARATOR)
            append("url")
            append(SEPARATOR)
            append("username")
            append(SEPARATOR)
            append("value")
        }
    }

    private fun parsePassword(password: Password): String {
        return "${password.name}$SEPARATOR${password.url}$SEPARATOR${password.username}$SEPARATOR${password.value}$SEPARATOR"
    }

    private fun unparsePassword(password: String): Password {
        val parts = password.split(SEPARATOR)
        return Password(name = parts[0], url = parts[1], username = parts[2], value = parts[3])
    }
    
    fun parsePasswordsWithHeader(passwords: List<Password>): String {
        return buildString {
            appendLine(HEADER)
            passwords.forEach { password ->
                appendLine(parsePassword(password))
            }
        }.trimEnd()
    }

    fun unparsePasswordsWithHeader(passwords: String): List<Password> {
        if (passwords.isBlank()) return emptyList()

        val lines = passwords.trim().split("\n", "\r\n")
        return if (lines.size > 1) {
            lines.subList(1, lines.size).map { line ->
                unparsePassword(line)
            }
        } else {
            emptyList()
        }
    }
}