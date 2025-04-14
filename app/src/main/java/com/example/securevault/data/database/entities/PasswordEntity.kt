package com.example.securevault.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name:String,
    @ColumnInfo(name = "url")
    val url:String,
    @ColumnInfo(name = "username")
    val username:String,
    @ColumnInfo(name = "value")
    val value:String
)

