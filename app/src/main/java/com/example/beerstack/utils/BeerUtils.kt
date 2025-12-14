package com.example.beerstack.utils

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.beerstack.components.BeerItemCard
import com.example.beerstack.components.SortOptions
import com.example.beerstack.model.Beer
import com.example.beerstack.model.Currency

//Show from the API the beers (now temporary the test data) in a scrolling list, using LazyColumn
// BeerList = for scrollable list
@Composable
fun BeerList(
    items: List<Beer>,
    onAddBeerClick: (Beer) -> Unit,
    currency: Currency,
    eurPerUsd: Double
) {
    LazyColumn {                        // lazy scrollable column for performance
        items(items) { beer ->          // loop through each beer in the list
            BeerItemCard(
                beer = beer,            // pass beer data
                onAddToCollection = { onAddBeerClick(beer) },   //To add beer to collectoin
                currency = currency,    // currency for price display
                eurPerUsd = eurPerUsd   // exchange rate for conversion
            )
        }
    }
}

//The sort dropdown functionality
fun sortBeers(beers: List<Beer>, sortOption: SortOptions): List<Beer> {
    return when (sortOption) {
        SortOptions.NAME -> beers.sortedBy { it.name }
        SortOptions.NAME_REVERSE -> beers.sortedByDescending { it.name }
        SortOptions.PRICE -> beers.sortedBy { it.price?.replace("$", "")?.toDoubleOrNull() ?: Double.MAX_VALUE }
        SortOptions.PRICE_REVERSE -> beers.sortedByDescending { it.price?.replace("$", "")?.toDoubleOrNull() ?: Double.MIN_VALUE }
        SortOptions.RATING -> beers.sortedBy { it.rating?.average ?: Double.MIN_VALUE }
        SortOptions.RATING_REVERSE -> beers.sortedByDescending { it.rating?.average ?: Double.MAX_VALUE }
    }
}

// To show symbol of price (EUR/USD)
// To convert symbols and currency rates
fun formatBeerPrice(
    rawPrice: String,
    beerCurrency: String,
    appCurrency: Currency,   // enum uit je model
    eurPerUsd: Double
): String {
    val base = rawPrice.toDoubleOrNull() ?: return "" // safely parse price

    // Convert to EUR if original is USD
    val priceInEur = when (beerCurrency.uppercase()) {
        "USD" -> base * eurPerUsd  // USD -> EUR
        else  -> base               // EUR stays EUR
    }

    // Convert USD price to app currency (EUR)
    val finalAmount = when (appCurrency) {
        Currency.EUR -> priceInEur
        Currency.USD -> priceInEur / eurPerUsd
    }

    // Determine symbol to display
    val symbol = when (appCurrency) {
        Currency.EUR -> "€"
        Currency.USD -> "$"
    }

    // Return formatted string with 2 decimal places
    return "%s%.2f".format(symbol, finalAmount)
}