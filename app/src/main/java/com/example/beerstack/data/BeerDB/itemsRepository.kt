package com.example.beerstack.data.BeerDB

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface ItemsRepository {
    fun getAllUsersWithBeer(): Flow<List<UserwithBeer>>
    fun getItemStream(id: Int): Flow<Item?>
    suspend fun insertItem(item: Item)
    suspend fun deleteItem(item: Item)
    suspend fun updateItem(item: Item)

    // Only declare the function, don’t call DAO here
    fun getItemsByOwner(ownerId: Int): Flow<List<Item>>
}
