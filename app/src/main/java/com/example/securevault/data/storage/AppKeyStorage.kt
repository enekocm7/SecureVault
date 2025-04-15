package com.example.securevault.data.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppKeyStorage @Inject constructor(@ApplicationContext context: Context) {

    private val prefs : SharedPreferences =  context.getSharedPreferences("app_key_prefs", Context.MODE_PRIVATE)

    fun save(name : String, data: ByteArray){
        prefs.edit { putString(name, Base64.encodeToString(data, Base64.NO_WRAP)) }
    }

    fun get(name: String) : ByteArray? {
        val encoded = prefs.getString(name,null) ?: return null
        return Base64.decode(encoded, Base64.NO_WRAP)
    }

    fun isPasswordConfigured(): Boolean {
        return prefs.contains("encrypted_app_key_pw") &&
                prefs.contains("iv_pw") &&
                prefs.contains("salt")
    }

    fun isBiometricConfigured(): Boolean{
        return prefs.contains("encrypted_app_key_bio") &&
                prefs.contains("iv_bio")
    }

}
