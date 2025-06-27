package com.enekocm.securevault.domain.usecases.firestore

import com.enekocm.securevault.data.firestore.FirestoreModel
import com.enekocm.securevault.data.storage.FirebaseStorage
import com.enekocm.securevault.domain.model.Preferences
import javax.inject.Inject

class ModelToPreferences @Inject constructor(private val firebaseStorage: FirebaseStorage) {
    operator fun invoke(model: FirestoreModel): Preferences = firebaseStorage.modelToPreferences(model)
}