package com.example.securevault.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.securevault.data.database.entities.PasswordEntity

@Dao
interface PasswordDao {
    @Query("Select * from passwords")
    suspend fun getAllPasswords(): List<PasswordEntity>

    @Query("Select * from passwords p where p.name = :name")
    suspend fun getPasswordByName(name:String): PasswordEntity

    @Query("Select * from passwords order by name asc")
    suspend fun getPasswordsOrderByName(): List<PasswordEntity>

    @Upsert
    suspend fun insertPassword(password: PasswordEntity)

    @Upsert
    suspend fun insertPasswords(passwords: List<PasswordEntity>)

    @Delete
    suspend fun deletePassword(password: PasswordEntity)
}