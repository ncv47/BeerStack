package com.example.beerstack.data.UserDB

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [User] from a given data source.
 */
interface UsersRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllItemsStream(): Flow<List<User>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: Int): Flow<User?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: User)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: User)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: User)
}//werk