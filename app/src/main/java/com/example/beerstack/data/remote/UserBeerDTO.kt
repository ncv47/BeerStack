package com.example.beerstack.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class UserBeerDto(
    val id: Long? = null,
    val userid: Int,
    val beerid: Int,
    val name: String,
    val price: String? = null,
    val myrating: Double,
    val apiaverage: Double,
    val imageurl: String? = null
)