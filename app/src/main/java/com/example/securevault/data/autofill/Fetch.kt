package com.example.securevault.data.autofill

import com.example.securevault.domain.repository.PasswordRepository

object Fetch {
	fun fetchPassword(
		appPackage: String, passwordRepository: PasswordRepository
	): Pair<String, String>? {
		val passwords = passwordRepository.getAllPasswords()

		val match = passwords.firstOrNull { password ->
			val urlTokens = password.url.split('.')
			val nameToken = password.name.lowercase()

			urlTokens.any { appPackage.contains(it, ignoreCase = true) } ||
					appPackage.contains(nameToken, ignoreCase = true)
		} ?: return null

		return Pair(match.username, match.value)
	}
}