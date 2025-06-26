package com.enekocm.securevault.domain.usecases.firestore

import com.enekocm.securevault.data.firestore.FirestoreModel
import com.enekocm.securevault.data.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class GetModel @Inject constructor(private val storage: FirebaseStorage, private val auth: FirebaseAuth) {
    suspend operator fun invoke(): FirestoreModel? {
        val uid = auth.uid ?: return null
        return  storage.getPasswords(uid)
    }
}