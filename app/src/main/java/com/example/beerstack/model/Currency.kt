package com.example.beerstack.model

enum class Currency {
    USD, EUR
}

data class EuroRatesResponse(
    val eur: Map<String, Double>
)