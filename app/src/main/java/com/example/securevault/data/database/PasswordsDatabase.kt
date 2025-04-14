package com.example.securevault.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.securevault.data.database.dao.PasswordDao
import com.example.securevault.data.database.entities.PasswordEntity

@Database(
    entities = [PasswordEntity::class],
    version = 1
)
abstract class PasswordsDatabase: RoomDatabase() {

    abstract fun getPasswordsDao(): PasswordDao

    companion object{
        @Volatile
        private var INSTACE: PasswordsDatabase? = null

        fun getDatabase(context: Context): PasswordsDatabase{
            return INSTACE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PasswordsDatabase::class.java,
                    "secureVault"
                ).build()
                INSTACE = instance
                return instance
            }
        }
    }
}