package com.enekocm.securevault.data.storage

import com.enekocm.securevault.data.firestore.FirestoreModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseStorage @Inject constructor(
    private val db: FirebaseFirestore,
    private val appKeyStorage: AppKeyStorage
) {
    companion object {
        const val COLLECTION = "users"
        const val PASSWORDS = "passwords"
        const val KEY = "derivedKey"
        const val SALT = "salt"
        const val IV = "iv"
    }

    fun savePasswords(model: FirestoreModel): Boolean {
        var isSuccess = false
        db.collection(COLLECTION)
            .document(model.uid)
            .set(model)
            .addOnSuccessListener { isSuccess = true }
            .addOnFailureListener { isSuccess = false }
        return isSuccess
    }

    suspend fun getPasswords(uid: String?): FirestoreModel? {
        if (uid == null) return null
        return try {
            val doc = db.collection(COLLECTION).document(uid).get().await()
            if (doc != null && doc.exists()) {
                FirestoreModel(
                    uid = uid,
                    passwords = doc.getString(PASSWORDS)!!,
                    derivedKey = doc.getString(KEY)!!,
                    salt = doc.getString(SALT)!!,
                    iv = doc.getString(IV)!!
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    fun savePreferences(model: FirestoreModel){
        appKeyStorage.save("salt", model.salt.toByteArray())
        appKeyStorage.save("encrypted_app_key_pw", model.derivedKey.toByteArray())
        appKeyStorage.save("iv_pw", model.iv.toByteArray())
    }

}