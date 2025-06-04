package com.enekocm.securevault.data.autofill

import android.app.assist.AssistStructure
import android.view.autofill.AutofillId
import com.enekocm.securevault.data.autofill.entities.Credentials
import com.enekocm.securevault.data.autofill.entities.ParsedStructure
import kotlin.text.contains

object StructureParser {

	fun parseStructure(structure: AssistStructure): ParsedStructure? {
		var usernameId: AutofillId? = null
		var passwordId: AutofillId? = null

		val nodes = structure.windowNodeCount
		for (i in 0 until nodes) {
			val windowNode = structure.getWindowNodeAt(i)
			val viewNode = windowNode.rootViewNode
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
		return hint.contains("user") || hint.contains("email") ||
				hint.contains("usuario") || hint.contains("correo") ||
				idEntry.contains("user") || idEntry.contains("usuario") ||
				autofillHints.contains("username") || autofillHints.contains("email") ||
				autofillHints.contains("usuario") || autofillHints.contains("correo")
	}
	
	private fun isPasswordField(node: AssistStructure.ViewNode): Boolean {
		val hint = node.hint?.lowercase() ?: ""
		val idEntry = node.idEntry?.lowercase() ?: ""
		val autofillHints = node.autofillHints?.map { it.lowercase() } ?: emptyList()
		return hint.contains("pass") || hint.contains("contraseña") ||
				idEntry.contains("pass") || idEntry.contains("contraseña") ||
				autofillHints.contains("password") || autofillHints.contains("contraseña")
	}
}