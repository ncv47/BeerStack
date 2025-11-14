package com.example.beerstack.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://api.untappd.com/v4"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface BeerApiService {
    @GET("beers")
    suspend fun getBeers(): String
}

object BeerApi {
    val retrofitService: BeerApiService by lazy {
        retrofit.create(BeerApiService::class.java)
    }
}