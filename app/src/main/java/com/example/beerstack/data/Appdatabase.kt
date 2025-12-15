package com.example.beerstack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.beerstack.data.UserDB.User
import com.example.beerstack.data.UserDB.UserDao

//declares the database
@Database(
    entities = [User::class], //says which item to use
    version = 1, //uses version 1
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile //ensures the AppDatabase is thread-safe and visible across all threads immediately.
        private var Instance: AppDatabase? = null //there's only 1 room database

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) { //makes sure that 2 threads don't create the database at the same time
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, //creates the class
                    "app_database" //gives the database a name
                )
                    .build() //builds it
                    .also { Instance = it }
            }
        }
    }
}
