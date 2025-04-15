package com.example.securevault.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.securevault.data.database.dao.PasswordDao
import com.example.securevault.data.database.entities.PasswordEntity

@Database(
    entities = [PasswordEntity::class],
    version = 1
)
abstract class PasswordsDatabase: RoomDatabase() {

    abstract fun getPasswordsDao(): PasswordDao

}