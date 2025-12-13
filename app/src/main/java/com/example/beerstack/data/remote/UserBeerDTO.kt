package com.example.beerstack.data.remote

import android.os.Parcelable
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Parcelize
@Serializable
data class UserBeerDto(
    val id: Long? = null,
    val userid: Int,
    val beerid: Int,
    val name: String,
    val price: String? = null,
    val myrating: Double,
    val apiaverage: Double,
    val notes: String? = null,
    val location: String? = null,
    val imageurl: String? = null,
    val myphoto: String? = null
) : Parcelable

@Parcelize
@Serializable
data class BeerDto(
    val id: Long? = null,
    val name: String,
    val price: String,
    val apiaverage: Double,
    val reviews: Int,
    val imageurl: String? = null
) : Parcelable