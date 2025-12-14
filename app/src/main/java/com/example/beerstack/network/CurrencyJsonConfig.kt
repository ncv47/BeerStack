package com.example.beerstack.network

import kotlinx.serialization.json.Json

// This is for the currency API
// We only want USD -> EUR but the api contains a lot of currencies
// Use this to ignore all the other currencies and only parse USD & EUR
// Otherwise all other currencies need to included in the model
val jsonConfig: Json = Json {
    ignoreUnknownKeys = true
}