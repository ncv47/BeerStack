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
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    //Client from CertificatePinner.kt (import not necessary bcs same dir)
    .client(okHttpClient) // enables SSL pinning
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .build()


interface SampleBeersApiService {
    //HTTP Get request from beers/ale of the API (https://api.sampleapis.com/beers/ale)
    @GET("beers/ale")
    //Asynchronously fetch a list of Beer objects
    suspend fun getBeers(): List<Beer>

    //To get Beers per ID for the collection (2nd request)
    // https://api.sampleapis.com/beers/ale/5 fetches all the data of the beer with ID = 5
    @GET("beers/ale/{id}")
    suspend fun getBeerById(@Path("id") id: Int): Beer  // New endpoint
}

//Object, Single Instance
object SampleApi {
    //Use the lazy delegation
    val retrofitService: SampleBeersApiService by lazy {
        //Create helpr object form api interface
        //whenever call its methods, Retrofit automatically sends the actual HTTP request
        retrofit.create(SampleBeersApiService::class.java)
    }
}