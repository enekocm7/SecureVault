package domain.usecases.firestore

import data.firestore.FirestoreModel
import data.storage.FirebaseStorage
import domain.model.Preferences
import javax.inject.Inject

class ModelToPreferences @Inject constructor(private val firebaseStorage: FirebaseStorage?) {
    operator fun invoke(model: FirestoreModel): Preferences? = firebaseStorage?.modelToPreferences(model)
}
