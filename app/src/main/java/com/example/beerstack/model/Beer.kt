package com.example.beerstack.model

//all the values for the api (punk)
data class Beer(
    val id: Int,
    val name: String,
    val brewery: String?,
    val abv: String?,
    val style: String?,
    val ibu: Int?,
    val ounces: Int?
)