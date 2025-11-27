package com.example.beerstack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.beerstack.data.BeerDB.Item
import com.example.beerstack.data.BeerDB.ItemDao
import com.example.beerstack.data.UserDB.User
import com.example.beerstack.data.UserDB.UserDao

@Database(
    entities = [User::class, Item::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()  // optional but helpful during development
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
