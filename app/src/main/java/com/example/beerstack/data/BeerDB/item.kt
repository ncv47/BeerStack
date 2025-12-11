package com.example.beerstack.data.BeerDB

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.beerstack.data.UserDB.User
//werk

data class UserwithBeer(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userid",
        entityColumn = "ownerId"
    )
    val library: List<Item> // must be a list
)

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val beerid: Int = 0,
    val beername: String,
    val beerprice: Int,
    val beerimage: String? = null,
    val beerrating: Double = 0.0,
    val beeraverage: Double = 0.0,
    val ownerId: Int
)