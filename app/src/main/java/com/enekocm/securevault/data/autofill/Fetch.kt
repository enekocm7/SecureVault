package com.enekocm.securevault.data.autofill

import com.enekocm.securevault.data.autofill.entities.Credentials
import com.enekocm.securevault.data.json.model.Password

object Fetch {
	fun fetchPassword(
		appPackage: String,
		passwords: List<Password>,
		webDomain: String? = null
	): Credentials? {
		if (!webDomain.isNullOrEmpty()) {
			return findMatchByDomain(webDomain, passwords)
		}
		return findMatchByPackage(appPackage, passwords)
	}

	private fun findMatchByDomain(domain: String, passwords: List<Password>): Credentials? {
		val cleanDomain = domain.replace(Regex("^(https?://|www\\.)"), "")
			.split("/").firstOrNull() ?: return null

		val exactMatch = passwords.firstOrNull { password ->
			val passwordUrl = password.url.replace(Regex("^(https?://|www\\.)"), "")
				.split("/").firstOrNull() ?: ""

			passwordUrl.equals(cleanDomain, ignoreCase = true)
		}

		if (exactMatch != null) {
			return Credentials(exactMatch.username, exactMatch.value)
		}

		val domainParts = cleanDomain.split(".")
		if (domainParts.size >= 2) {
			val baseDomain = domainParts.takeLast(2).joinToString(".")

			val subdomainMatch = passwords.firstOrNull { password ->
				val passwordUrl = password.url.replace(Regex("^(https?://|www\\.)"), "")
					.split("/").firstOrNull() ?: ""

				passwordUrl.endsWith(baseDomain, ignoreCase = true)
			}

			if (subdomainMatch != null) {
				return Credentials(subdomainMatch.username, subdomainMatch.value)
			}
		}

		return null
	}

	private fun findMatchByPackage(appPackage: String, passwords: List<Password>): Credentials? {
		val password = passwords.firstOrNull { password ->
			val urlTokens = password.url.split('.').dropLast(1)
			val nameToken = password.name.lowercase()

			urlTokens.any { appPackage.contains(it, ignoreCase = true) } ||
					appPackage.contains(nameToken, ignoreCase = true)
		}
		return password?.let { Credentials(it.username, it.value) }
	}
}

