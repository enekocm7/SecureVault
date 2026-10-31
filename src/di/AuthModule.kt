package di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import javax.inject.Singleton

object AuthModule {

    @Singleton
    fun provideAuth(): FirebaseAuth{
        return Firebase.auth
    }
}