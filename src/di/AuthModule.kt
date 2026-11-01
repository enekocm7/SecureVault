package di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    /**
     * Null when Firebase isn't configured (no google-services.json at build
     * time): the app runs offline instead of crashing. Callers must use
     * [FirebaseAuth.currentUser] null-safe and fall back to local storage.
     */
    @Provides
    @Singleton
    fun provideAuth(): FirebaseAuth? {
        return try {
            Firebase.auth
        } catch (_: Exception) {
            null
        }
    }
}
