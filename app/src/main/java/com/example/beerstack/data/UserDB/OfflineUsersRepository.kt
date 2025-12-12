package com.example.beerstack.data.UserDB

import kotlinx.coroutines.flow.Flow

class OfflineUsersRepository(private val userDao: UserDao) : UsersRepository {

    override fun getAllUsers(): Flow<List<User>> = userDao.getAllItems()

    override fun getUserById(id: Int): Flow<User?> = userDao.getItem(id)

    override suspend fun insert(user: User) = userDao.insert(user)

    override suspend fun delete(user: User) = userDao.delete(user)

    override suspend fun update(user: User) = userDao.update(user)

    override suspend fun login(username: String, password: String): User? =
        userDao.login(username, password)

    suspend fun isUsernameTaken(username: String): Boolean {
        return userDao.isUsernameTaken(username)
    }

}