package com.example.beerstack.model

import kotlinx.serialization.Serializable

//all the values for the api (punk)
data class Beer(
    val id: Int,
    val name: String,
    val price: String?,
    val image: String? = null, //sometimes no given input, standard is null value
    val rating: Rating? = null
)

//The rating is stored in json with 2 diffrent values,
// the average review /5 and how many reviews it has
//Serialization = save temporary in memory
@Serializable
data class Rating(
    val average: Double,
    val reviews: Int
)
