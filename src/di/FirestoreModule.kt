package di

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import javax.inject.Singleton

object FirestoreModule {

    @Singleton
    fun provideFirestoreDatabase(): FirebaseFirestore {
        return Firebase.firestore
    }
}