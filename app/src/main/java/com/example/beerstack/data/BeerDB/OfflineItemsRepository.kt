package com.example.beerstack.data.BeerDB
import kotlinx.coroutines.flow.Flow
//werk
class OfflineItemsRepository(private val itemDao: ItemDao) : ItemsRepository {
    override fun getAllUsersWithBeer(): Flow<List<UserwithBeer>> = itemDao.getAllUsersWithBeer()

    override fun getItemStream(id: Int): Flow<Item?> = itemDao.getItem(id)

    override suspend fun insertItem(item: Item) = itemDao.insert(item)

    override suspend fun deleteItem(item: Item) = itemDao.delete(item)

    override suspend fun updateItem(item: Item) = itemDao.update(item)
}