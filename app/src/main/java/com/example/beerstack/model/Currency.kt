package com.example.beerstack.model

import kotlinx.serialization.Serializable

enum class Currency {
    USD,
    EUR
}

// Response JSON looks like:
// { "date":"2025-01-01", "eur": { "usd": 1.08, ... }  }
@Serializable
data class EurRatesResponse(
    val date: String,
    val eur: EurRates
)

@Serializable
data class EurRates(
    val usd: Double
)