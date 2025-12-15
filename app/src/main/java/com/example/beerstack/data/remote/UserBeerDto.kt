package com.example.beerstack.data.remote

import android.os.Parcelable
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Parcelize
@Serializable
data class UserBeerDto(
    val id: Int? = null, //Let ID be decided by API (sequential)
    val userid: Int,
    val beerid: Int,
    val name: String,
    val currency: String? = null,
    val price: Double? = null,
    val myrating: Double?,
    val apiaverage: Double?,
    val notes: String? = null,
    val location: String? = null,
    val imageurl: String? = null,
    val myphoto: String? = null,
    val date: String? = null
) : Parcelable // Implements Parcelable to allow easy passing between Activities/Fragments

@Parcelize
@Serializable
data class SupabaseBeerDto(
    val id: Long? = null,
    val name: String,
    val currency: String,
    val price: String,
    val apiaverage: Double,
    val reviews: Int,
    val imageurl: String? = null
) : Parcelable