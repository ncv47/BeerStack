package com.example.beerstack.network

import com.example.beerstack.model.BeerDto
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.http.GET

// --- Supabase settings ---
private const val SUPABASE_URL = "https://dqkpzojnslcyeluzzyhi.supabase.co/"
private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRxa3B6b2puc2xjeWVsdXp6eWhpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjU0NTYzODgsImV4cCI6MjA4MTAzMjM4OH0.wh3Id22_gXhKNzjIVbyZRUHnU4zhSayxXykxxQNS0WM"   // put anon key here

// Add headers for every request
class SupabaseAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("apikey", SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_ANON_KEY")
            .build()
        return chain.proceed(request)
    }
}

private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(SupabaseAuthInterceptor())
    .build()

// Configure Kotlinx Serialization for Retrofit
private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

private val retrofit = Retrofit.Builder()
    .baseUrl(SUPABASE_URL)
    .client(okHttpClient)
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()

interface SupabaseBeersApiService {
    // GET /rest/v1/Beers?select=*
    @GET("rest/v1/Beers")
    suspend fun getBeers(): List<BeerDto>
}

// Single instance
object SupabaseApi {
    val retrofitService: SupabaseBeersApiService by lazy {
        retrofit.create(SupabaseBeersApiService::class.java)
    }
}
