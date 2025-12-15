package com.example.beerstack.data.UserDB
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: User)

    @Update
    suspend fun update(item: User)

    @Delete
    suspend fun delete(item: User)

    @Query("SELECT * from users WHERE userid = :id")
    fun getItem(id: Int): Flow<User>

    @Query("SELECT * from users ORDER BY userName ASC")
    fun getAllItems(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE userName = :username AND userPassword = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?
    //if the username is taken it will return the username, if not it will return null
    suspend fun isUsernameTaken(username: String): Boolean {
        return getUserByUsername(username) != null
    }
    //takes an username and matches it against the database
    @Query("SELECT * FROM users WHERE LOWER(userName) = LOWER(:username) LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

}
