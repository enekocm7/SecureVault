package com.enekocm.securevault.di

import com.enekocm.securevault.data.repository.MasterPasswordRepositoryImpl
import com.enekocm.securevault.data.storage.AppKeyStorage
import com.enekocm.securevault.domain.repository.MasterPasswordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MasterPasswordRepositoryModule {

    @Provides
    @Singleton
    fun provideMasterPasswordRepository(storage: AppKeyStorage): MasterPasswordRepository {
        return MasterPasswordRepositoryImpl(storage)
    }
}