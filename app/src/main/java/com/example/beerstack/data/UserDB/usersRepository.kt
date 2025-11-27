package com.example.beerstack.data.UserDB

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [User] from a given data source.
 */
interface UsersRepository {
    fun getAllUsers(): Flow<List<User>>
    fun getUserById(id: Int): Flow<User?>
    suspend fun insert(user: User)
    suspend fun delete(user: User)
    suspend fun update(user: User)
    suspend fun login(username: String, password: String): User?
}

