package com.example.beerstack.data.BeerDB
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * from items WHERE beerid = :id")
    fun getItem(id: Int): Flow<Item>

    @Transaction
    @Query("SELECT * FROM users ORDER BY userName ASC")
    fun getAllUsersWithBeer(): Flow<List<UserwithBeer>>

}