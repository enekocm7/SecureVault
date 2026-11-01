package di

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirestoreModule {

    /**
     * Null when Firebase isn't configured (no google-services.json at build
     * time): the app runs offline instead of crashing.
     */
    @Provides
    @Singleton
    fun provideFirestoreDatabase(): FirebaseFirestore? {
        return try {
            Firebase.firestore
        } catch (_: Exception) {
            null
        }
    }
}
