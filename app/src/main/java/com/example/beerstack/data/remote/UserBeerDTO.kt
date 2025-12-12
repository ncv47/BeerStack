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
    val imageurl: String? = null,
    val myphoto: String? = null
) : Parcelable