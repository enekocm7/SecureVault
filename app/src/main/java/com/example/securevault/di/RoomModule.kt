package com.example.securevault.di

import android.content.Context
import androidx.room.Room
import com.example.securevault.data.database.PasswordsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val PASSWORD_DATABASE_NAME = "password_database"

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context:Context) =
        Room.databaseBuilder(context, PasswordsDatabase::class.java, PASSWORD_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun providePasswordDao(db: PasswordsDatabase) = db.getPasswordsDao()
}