package com.example.beerstack.data.UserDB

import kotlinx.coroutines.flow.Flow

class OfflineUsersRepository(private val itemDao: UserDao) : UsersRepository {
    override fun getAllItemsStream(): Flow<List<com.example.beerstack.data.UserDB.User>> = itemDao.getAllItems()

    override fun getItemStream(id: Int): Flow<com.example.beerstack.data.UserDB.User?> = itemDao.getItem(id)

    override suspend fun insertItem(item: com.example.beerstack.data.UserDB.User) = itemDao.insert(item)

    override suspend fun deleteItem(item: com.example.beerstack.data.UserDB.User) = itemDao.delete(item)

    override suspend fun updateItem(item: User) = itemDao.update(item)
}