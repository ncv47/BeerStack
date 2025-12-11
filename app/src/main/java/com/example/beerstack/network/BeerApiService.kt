package com.example.beerstack.network

import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import com.example.beerstack.model.Beer

//The used API (sampleAPIs)
private const val BASE_URL = "https://api.sampleapis.com/"

//Retrofit instance with given arguments: URL for API request, Gson converter to parse JSON into data classes
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.sampleapis.com/")
    .client(okHttpClient) // enables the 2nd method for SSL pinning
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .build()

interface SampleBeersApiService {
    //HTTP Get request from beers/ale of the API (https://api.sampleapis.com/beers/ale)
    @GET("beers/ale")
    //Asynchronously fetch a list of Beer objects
    suspend fun getBeers(): List<Beer>
}

//Object, Single Instance
object SampleApi {
    //Use the lazy delegation
    val retrofitService: SampleBeersApiService by lazy {
        //Create helper object form api interface
        //whenever call its methods, Retrofit automatically sends the actual HTTP request
        retrofit.create(SampleBeersApiService::class.java)
    }
}