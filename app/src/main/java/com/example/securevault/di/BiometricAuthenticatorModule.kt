package com.example.securevault.di

import androidx.appcompat.app.AppCompatActivity
import com.example.securevault.domain.biometric.BiometricAuthenticator
import com.example.securevault.ui.biometrics.BiometricPromptManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricPromptManagerFactory @Inject constructor() {
    fun create(activity: AppCompatActivity): BiometricAuthenticator {
        return BiometricPromptManager(activity)
    }
}


@Module
@InstallIn(SingletonComponent::class)
object BiometricAuthenticatorModule {

    @Provides
    @Singleton
    fun provideBiometricAuthenticatorFactory(): BiometricPromptManagerFactory {
        return BiometricPromptManagerFactory()
    }
}
