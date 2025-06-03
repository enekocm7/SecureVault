package com.enekocm.securevault.data.autofill

import android.app.assist.AssistStructure
import android.view.autofill.AutofillId
import com.enekocm.securevault.data.autofill.entities.Credentials
import com.enekocm.securevault.data.autofill.entities.ParsedStructure

object StructureParser {

	fun parseStructure(structure: AssistStructure): ParsedStructure? {
		var usernameId: AutofillId? = null
		var passwordId: AutofillId? = null

		val nodes = structure.windowNodeCount
		for (i in 0 until nodes) {
			val windowNode = structure.getWindowNodeAt(i)
			val viewNode = windowNode.rootViewNode
			traverseViewNode(viewNode) { node ->
				val hint = node.hint?.lowercase() ?: ""
				val idEntry = node.idEntry?.lowercase() ?: ""
				val autofillHints = node.autofillHints?.map { it.lowercase() } ?: emptyList()

				if (usernameId == null && (hint.contains("user") || hint.contains("email") ||
							idEntry.contains("user") || autofillHints.contains("username") || autofillHints.contains(
						"email"
					))
				) {
					usernameId = node.autofillId
				}
				if (passwordId == null && (hint.contains("pass") ||
							idEntry.contains("pass") || autofillHints.contains("password"))
				) {
					passwordId = node.autofillId
				}
			}
		}
		if(usernameId == null || passwordId == null) {
			return null
		}

		return ParsedStructure(usernameId!!, passwordId!!)
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
					val hint = node.hint?.lowercase() ?: ""
					val idEntry = node.idEntry?.lowercase() ?: ""
					val autofillHints = node.autofillHints?.map { it.lowercase() } ?: emptyList()

					if (username == null && (hint.contains("user") || hint.contains("email") ||
								idEntry.contains("user") || autofillHints.contains("username") ||
								autofillHints.contains("email"))
					) {
						username = autofillValue.textValue.toString()
					}

					if (password == null && (hint.contains("pass") ||
								idEntry.contains("pass") || autofillHints.contains("password"))
					) {
						password = autofillValue.textValue.toString()
					}
				}
			}
		}

		return Credentials(username, password)
	}
}