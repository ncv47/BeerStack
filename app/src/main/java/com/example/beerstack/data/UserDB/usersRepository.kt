package com.example.beerstack.data.UserDB

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [User] from a given data source.
 */
interface UsersRepository {
    fun getAllItemsStream(): Flow<List<User>>
    fun getItemStream(id: Int): Flow<User?>
    suspend fun insertItem(item: User)
    suspend fun deleteItem(item: User)
    suspend fun updateItem(item: User)

    suspend fun login(username: String, password: String): User?
}//werk