package com.example.beerstack.data.BeerDB
//werk
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class BeerDatabase  : RoomDatabase()  {
    abstract fun itemDao(): ItemDao //makes sure that the database knows about the DAO
    companion object{ //allows access to the methods to create or get the database and uses the class name as the qualifier.
        @Volatile
        private var Instance: BeerDatabase? = null //makes sure that there's only 1 database opened at a given time
        fun getDatabase(context: Context): BeerDatabase {
            return Instance ?: synchronized(this) {// prevents race condition (2 database instances at the same time)
                Room.databaseBuilder(context, BeerDatabase::class.java, "item_database")
                    .build() //To create the database instance, removes the android studio errors
                    .also {Instance = it}
            }

        }
    }
}