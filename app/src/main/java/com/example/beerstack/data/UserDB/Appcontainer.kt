package com.example.beerstack.data.UserDB

import android.content.Context
import com.example.beerstack.data.UserDB.UserDatabase
import com.example.beerstack.data.UserDB.UsersRepository
import com.example.beerstack.data.UserDB.OfflineUsersRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val usersRepository: UsersRepository
}
//werk
/**
 * [AppContainer] implementation that provides instance of [OfflineUsersRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [UsersRepository]
     */
    override val usersRepository: UsersRepository by lazy {
        OfflineUsersRepository(UserDatabase.getDatabase(context).itemDao())
    }
}