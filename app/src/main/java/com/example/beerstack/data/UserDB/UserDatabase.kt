package com.example.beerstack.data.UserDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.beerstack.data.UserDB.User
import com.example.beerstack.data.UserDB.UserDao

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase  : RoomDatabase()  {
    abstract fun itemDao(): UserDao //makes sure that the database knows about the DAO
    companion object{ //allows access to the methods to create or get the database and uses the class name as the qualifier.
        @Volatile
        private var Instance: UserDatabase? = null //makes sure that there's only 1 database opened at a given time
        fun getDatabase(context: Context): UserDatabase {
            return Instance ?: synchronized(this) {// prevents race condition (2 database instances at the same time)
                Room.databaseBuilder(context, UserDatabase::class.java, "user_database")
                    .build() //To create the database instance, removes the android studio errors
                    .also {Instance = it}
            }

        }
    }
}