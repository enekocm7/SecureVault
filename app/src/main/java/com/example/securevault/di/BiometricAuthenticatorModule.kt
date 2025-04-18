package com.example.securevault.di

import com.example.securevault.ui.biometrics.BiometricPromptManagerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BiometricAuthenticatorModule {

    @Provides
    @Singleton
    fun provideBiometricAuthenticatorFactory(): BiometricPromptManagerFactory {
        return BiometricPromptManagerFactory()
    }
}
