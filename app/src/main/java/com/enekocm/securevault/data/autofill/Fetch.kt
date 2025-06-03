package com.enekocm.securevault.data.autofill

import com.enekocm.securevault.data.json.model.Password

object Fetch {
	fun fetchPassword(
		appPackage: String, passwords: List<Password>
	): Pair<String?, String?> {

		val match = passwords.firstOrNull { password ->
			val urlTokens = password.url.split('.').dropLast(1)
			val nameToken = password.name.lowercase()

			urlTokens.any { appPackage.contains(it, ignoreCase = true) } ||
					appPackage.contains(nameToken, ignoreCase = true)
		} ?: return Pair(null,null)

		return Pair(match.username, match.value)
	}
}