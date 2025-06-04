package com.enekocm.securevault.data.autofill.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.service.autofill.InlinePresentation
import android.service.autofill.SaveInfo
import android.view.autofill.AutofillId
import android.view.inputmethod.InlineSuggestionsRequest
import androidx.autofill.inline.v1.InlineSuggestionUi
import com.enekocm.securevault.R
import com.enekocm.securevault.data.autofill.entities.ParsedStructure
import com.enekocm.securevault.data.crypto.AppKeyProvider
import com.enekocm.securevault.ui.view.LoginActivity

object Utils {

    fun createLoginIntent(
        context: Context,
        appPackage: String = "",
        usernameId: AutofillId? = null,
        passwordId: AutofillId? = null,
        webDomain: String? = null
    ): PendingIntent {
        return PendingIntent.getActivity(
            context,
            0,
            Intent(context, LoginActivity::class.java).apply {
                putExtra("package", appPackage)
                putExtra("usernameId", usernameId)
                putExtra("passwordId", passwordId)
                putExtra("webDomain", webDomain)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @SuppressLint("RestrictedApi")
    fun createInlinePresentation(
        context: Context,
        request: InlineSuggestionsRequest?,
        text: String,
        pendingIntent: PendingIntent = createLoginIntent(context)
    ): InlinePresentation? {
        request ?: return null
        if (request.maxSuggestionCount <= 0) return null

        val spec = request.inlinePresentationSpecs.firstOrNull() ?: return null

        return InlinePresentation(
            InlineSuggestionUi.newContentBuilder(pendingIntent).apply {
                setTitle(text)
                setStartIcon(
                    Icon.createWithResource(
                        context,
                        R.mipmap.ic_launcher_round
                    )
                )
            }.build().slice,
            spec,
            false
        )
    }

    fun getSaveInfo(parsedStructure: ParsedStructure): SaveInfo{
        return SaveInfo.Builder(
            SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD,
            arrayOf(parsedStructure.usernameId, parsedStructure.passwordId)
        ).build()
    }

    suspend fun isAppKeyAvailable(): Boolean {
        return try {
            AppKeyProvider.getAppKey()
            true
        } catch (_: Exception) {
            false
        }
    }
}

