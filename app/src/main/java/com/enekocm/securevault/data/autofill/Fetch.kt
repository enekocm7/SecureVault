package com.enekocm.securevault.data.autofill

import com.enekocm.securevault.data.autofill.entities.Credentials
import com.enekocm.securevault.data.json.model.Password

object Fetch {
	fun fetchPassword(
		appPackage: String, passwords: List<Password>
	): Credentials? {

		val match = passwords.firstOrNull { password ->
			val urlTokens = password.url.split('.').dropLast(1)
			val nameToken = password.name.lowercase()

			urlTokens.any { appPackage.contains(it, ignoreCase = true) } ||
					appPackage.contains(nameToken, ignoreCase = true)
		} ?: return null

		return Credentials(match.username, match.value)
	}
}