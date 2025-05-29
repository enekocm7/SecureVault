package com.enekocm.securevault.data.storage

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BackupStorage @Inject constructor(@ApplicationContext private val context: Context) {

    private val prefs = context.getSharedPreferences("backup_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val LOCATION = "backup_location"
        private const val ENABLED = "backup_enabled"
    }

    fun saveLocation(uri: Uri) {
        prefs.edit(commit = true) { putString(LOCATION, uri.toString()) }
    }

    fun getLocation(): Uri? {
        return prefs.getString(LOCATION, null)?.toUri()
    }

    fun enableBackup() {
        prefs.edit { putBoolean(ENABLED,true) }
    }

    fun disableBackup(){
        prefs.edit { putBoolean(ENABLED,false) }
    }

    fun isBackupEnabled(): Boolean {
        return prefs.getBoolean(ENABLED,false)
    }

}