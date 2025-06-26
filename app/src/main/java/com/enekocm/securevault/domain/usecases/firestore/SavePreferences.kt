package com.enekocm.securevault.domain.usecases.firestore

import com.enekocm.securevault.data.firestore.FirestoreModel
import com.enekocm.securevault.data.storage.FirebaseStorage
import javax.inject.Inject

class SavePreferences @Inject constructor(private val firebaseStorage: FirebaseStorage) {
    operator fun invoke(model: FirestoreModel) = firebaseStorage.savePreferences(model)
}