package com.example.beerstack.data

import android.content.Context
import com.example.beerstack.data.UserDB.UsersRepository
import com.example.beerstack.data.UserDB.OfflineUsersRepository


class AppDataContainer(context: Context) {

    // Access your Room database
    private val db = AppDatabase.getDatabase(context)


    // User repository
    val usersRepository: UsersRepository = OfflineUsersRepository(db.userDao())
}
