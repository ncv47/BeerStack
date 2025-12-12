package com.example.beerstack.model

import kotlinx.serialization.Serializable

@Serializable
data class BeerDto(
    val beerid: Int,
    val name: String,
    val price: String,
    val apiaverage: Double?,
    val reviews: Int?,
    val imageurl: String?
)

// Mapper function: BeerDto -> Beer used in UI
fun BeerDto.toBeer(): Beer = Beer(
    id = beerid,
    name = name,
    price = price,
    image = imageurl,
    rating = if (apiaverage != null && reviews != null)
        Rating(average = apiaverage, reviews = reviews)
    else null
)
