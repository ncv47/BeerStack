package com.example.beerstack.data.UserDB

import androidx.room.Entity
import androidx.room.PrimaryKey
//werk
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userid: Int = 0,
    val userName: String,
    val userPassword: String,
)
