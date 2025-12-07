package com.example.beerstack.network

import retrofit2.Retrofit
import retrofit2.http.GET
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import com.example.beerstack.model.EurRatesResponse

// Base URL is everything before "eur.json"
private const val CURRENCY_BASE_URL =
    "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/"

private val currencyRetrofit: Retrofit = Retrofit.Builder()
    .baseUrl(CURRENCY_BASE_URL)
    .addConverterFactory(jsonConfig.asConverterFactory("application/json".toMediaType()))
    .build()

interface CurrencyApiService {
    @GET("eur.json")
    suspend fun getEurRates(): EurRatesResponse   // from the model
}

object CurrencyApi {
    val retrofitService: CurrencyApiService by lazy {
        currencyRetrofit.create(CurrencyApiService::class.java)
    }
}

