package com.example.beerstack.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.beerstack.model.Beer

private const val BASE_URL = "https://api.sampleapis.com/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface SampleBeersApiService {
    @GET("beers/ale")
    suspend fun getBeers(): List<Beer>
}

object SampleApi {
    val retrofitService: SampleBeersApiService by lazy {
        retrofit.create(SampleBeersApiService::class.java)
    }
}