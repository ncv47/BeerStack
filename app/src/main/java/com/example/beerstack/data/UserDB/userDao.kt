package com.example.beerstack.data.UserDB
import com.example.beerstack.data.UserDB.User
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
    suspend fun insert(item: com.example.beerstack.data.UserDB.User)

    @Update
    suspend fun update(item: com.example.beerstack.data.UserDB.User)

    @Delete
    suspend fun delete(item: com.example.beerstack.data.UserDB.User)

    @Query("SELECT * from users WHERE userid = :id")
    fun getItem(id: Int): Flow<com.example.beerstack.data.UserDB.User>

    @Query("SELECT * from users ORDER BY userName ASC")
    fun getAllItems(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE userName = :username AND userPassword = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

}
