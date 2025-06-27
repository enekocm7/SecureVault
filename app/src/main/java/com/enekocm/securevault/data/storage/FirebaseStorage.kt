package com.enekocm.securevault.data.storage

import com.enekocm.securevault.data.firestore.FirestoreModel
import com.enekocm.securevault.domain.model.Preferences
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseStorage @Inject constructor(
    private val db: FirebaseFirestore
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
                    derivedKey = doc.getBlob(KEY)!!,
                    salt = doc.getBlob(SALT)!!,
                    iv = doc.getBlob(IV)!!
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    fun modelToPreferences(model: FirestoreModel): Preferences {
        return Preferences(key = model.derivedKey, salt = model.salt, iv = model.iv)
    }

}