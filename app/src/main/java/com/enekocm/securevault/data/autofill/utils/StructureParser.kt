package com.enekocm.securevault.data.autofill.utils

import android.app.assist.AssistStructure
import android.view.autofill.AutofillId
import android.webkit.WebView
import com.enekocm.securevault.data.autofill.entities.Credentials
import com.enekocm.securevault.data.autofill.entities.ParsedStructure
import java.net.URL

object StructureParser {

	fun parseStructure(structure: AssistStructure): ParsedStructure? {
		var usernameId: AutofillId? = null
		var passwordId: AutofillId? = null
		var webDomain: String? = null

		val nodes = structure.windowNodeCount
		for (i in 0 until nodes) {
			val windowNode = structure.getWindowNodeAt(i)
			val viewNode = windowNode.rootViewNode

			extractWebDomain(viewNode)?.let {
				webDomain = it
			}

			traverseViewNode(viewNode) { node ->
				if (isUsernameField(node) && usernameId == null) {
					usernameId = node.autofillId
				}
				if (isPasswordField(node) && passwordId == null) {
					passwordId = node.autofillId
				}
			}
		}
		if(usernameId == null || passwordId == null) {
			return null
		}

		return ParsedStructure(usernameId!!, passwordId!!, webDomain)
	}

	fun traverseViewNode(
        node: AssistStructure.ViewNode,
        action: (AssistStructure.ViewNode) -> Unit
	) {
		action(node)
		for (i in 0 until node.childCount) {
			traverseViewNode(node.getChildAt(i), action)
		}
	}

	fun extractCredentialsFromStructure(structure: AssistStructure): Credentials {
		var username: String? = null
		var password: String? = null

		for (i in 0 until structure.windowNodeCount) {
			val windowNode = structure.getWindowNodeAt(i)
			traverseViewNode(windowNode.rootViewNode) { node ->
				val autofillValue = node.autofillValue
				if (autofillValue != null && autofillValue.isText) {
					if (isUsernameField(node) && username == null) {
						username = autofillValue.textValue.toString()
					}

					if (isPasswordField(node) && password == null) {
						password = autofillValue.textValue.toString()
					}
				}
			}
		}

		return Credentials(username, password)
	}

	private fun isUsernameField(node: AssistStructure.ViewNode): Boolean {
		val hint = node.hint?.lowercase() ?: ""
		val idEntry = node.idEntry?.lowercase() ?: ""
		val autofillHints = node.autofillHints?.map { it.lowercase() } ?: emptyList()

		val htmlAttributes = getHtmlAttributes(node)
		val isWebUsername = htmlAttributes["type"] == "email" ||
			htmlAttributes["name"]?.contains("user") == true ||
			htmlAttributes["name"]?.contains("email") == true ||
			htmlAttributes["id"]?.contains("user") == true ||
			htmlAttributes["id"]?.contains("email") == true

		return hint.contains("user") || hint.contains("email") ||
				hint.contains("usuario") || hint.contains("correo") ||
				idEntry.contains("user") || idEntry.contains("usuario") ||
				autofillHints.contains("username") || autofillHints.contains("email") ||
				autofillHints.contains("usuario") || autofillHints.contains("correo") ||
				isWebUsername
	}

	private fun isPasswordField(node: AssistStructure.ViewNode): Boolean {
		val hint = node.hint?.lowercase() ?: ""
		val idEntry = node.idEntry?.lowercase() ?: ""
		val autofillHints = node.autofillHints?.map { it.lowercase() } ?: emptyList()

		val htmlAttributes = getHtmlAttributes(node)
		val isWebPassword = htmlAttributes["type"] == "password" ||
			htmlAttributes["name"]?.contains("pass") == true ||
			htmlAttributes["id"]?.contains("pass") == true

		return hint.contains("pass") || hint.contains("contraseña") ||
				idEntry.contains("pass") || idEntry.contains("contraseña") ||
				autofillHints.contains("password") || autofillHints.contains("contraseña") ||
				isWebPassword
	}

	private fun getHtmlAttributes(node: AssistStructure.ViewNode): Map<String, String> {
		val attributes = mutableMapOf<String, String>()
		val htmlInfo = node.htmlInfo ?: return attributes

		for (i in 0 until (htmlInfo.attributes?.size ?: 0)) {
			val attribute = htmlInfo.attributes?.get(i)
			attributes[attribute?.first?.lowercase() ?: ""] = attribute?.second?.lowercase() ?: ""
		}

		return attributes
	}

	private fun extractWebDomain(rootNode: AssistStructure.ViewNode): String? {
		var webDomain: String? = null

		traverseViewNode(rootNode) { node ->
			if (node.className == WebView::class.java.name) {
				webDomain = node.webDomain
				if (!webDomain.isNullOrEmpty()) return@traverseViewNode
			}

			if (webDomain.isNullOrEmpty() && node.htmlInfo != null) {
				val htmlAttributes = getHtmlAttributes(node)
				if (htmlAttributes.containsKey("action")) {
					val action = htmlAttributes["action"] ?: ""
					if (action.startsWith("http")) {
						try {
							val url = URL(action)
							webDomain = url.host
							if (!webDomain.isNullOrEmpty()) return@traverseViewNode
						} catch (_: Exception) { }
					}
				}

				if (webDomain.isNullOrEmpty() && node.webDomain != null) {
					webDomain = node.webDomain
					if (!webDomain.isNullOrEmpty()) return@traverseViewNode
				}
			}

			if (webDomain.isNullOrEmpty() && node.text != null) {
				val text = node.text.toString()
				if (text.startsWith("http")) {
					try {
						val url = URL(text)
						webDomain = url.host
						if (!webDomain.isNullOrEmpty()) return@traverseViewNode
					} catch (_: Exception) { }
				}
			}
		}
		return webDomain
	}
}