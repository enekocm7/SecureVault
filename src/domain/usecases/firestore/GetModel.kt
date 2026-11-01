package domain.usecases.firestore

import data.firestore.FirestoreModel
import data.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class GetModel @Inject constructor(private val storage: FirebaseStorage?, private val auth: FirebaseAuth?) {
    suspend operator fun invoke(): FirestoreModel? {
        val uid = auth?.currentUser?.uid ?: return null
        return storage?.getPasswords(uid)
    }
}
