package com.example.beerstack.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Retrofit
import retrofit2.http.GET
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

// Base URL is everything before "eur.json"
private const val CURRENCY_BASE_URL =
    "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/"

// Response JSON looks like:
// { "date": "2025-01-01", "eur": { "usd": 1.08, ... } }
@Serializable
data class EurRatesResponse(
    val date: String,
    val eur: EurRates
)

@Serializable
data class EurRates(
    val usd: Double
)

private val currencyRetrofit: Retrofit = Retrofit.Builder()
    .baseUrl(CURRENCY_BASE_URL)
    .addConverterFactory(jsonConfig.asConverterFactory("application/json".toMediaType()))
    .build()

interface CurrencyApiService {
    @GET("eur.json")
    suspend fun getEurRates(): EurRatesResponse   // from CurrencyDto.kt
}

object CurrencyApi {
    val retrofitService: CurrencyApiService by lazy {
        currencyRetrofit.create(CurrencyApiService::class.java)
    }
}

