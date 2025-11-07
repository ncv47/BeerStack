package com.example.beerstack.data.BeerDB

import androidx.room.Entity
import androidx.room.PrimaryKey
//werk
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val beerid: Int = 0,
    val beername: String,
    val beerprice: Int,
    val beerrating: Int
)