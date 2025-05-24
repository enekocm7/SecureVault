package com.example.securevault.data.autofill

import android.app.assist.AssistStructure
import android.view.autofill.AutofillId

object StructureParser {

	fun parseStructure(structure: AssistStructure): ParsedStructure {
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

				if (usernameId == null && hint.contains("user") || hint.contains("email") ||
					idEntry.contains("user") || autofillHints.contains("username") || autofillHints.contains("email")
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
		check(!(usernameId == null || passwordId == null)) {
			throw IllegalStateException("Did not find username or password fields")
		}

		return ParsedStructure(usernameId!!, passwordId!!)
	}

	private fun traverseViewNode(
		node: AssistStructure.ViewNode,
		action: (AssistStructure.ViewNode) -> Unit
	) {
		action(node)
		for (i in 0 until node.childCount) {
			traverseViewNode(node.getChildAt(i), action)
		}
	}


}

data class ParsedStructure(val usernameId: AutofillId, val passwordId: AutofillId)