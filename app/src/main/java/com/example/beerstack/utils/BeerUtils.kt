package com.example.beerstack.utils

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.beerstack.components.BeerItemCard
import com.example.beerstack.model.Beer
import com.example.beerstack.model.Currency

//Show from the API the beers (now temporary the test data) in a scrolling list, using LazyColumn
// beer = for scrollable list
// OnGetBeerByID = for the collection specific fetch
@Composable
fun BeerList(
    items: List<Beer>,
    onAddBeerClick: (Beer) -> Unit,
    currency: Currency,
    eurPerUsd: Double
) {
    LazyColumn {
        items(items) { beer ->
            BeerItemCard(
                beer = beer,
                onAddToCollection = { onAddBeerClick(beer) },
                currency = currency,
                eurPerUsd = eurPerUsd
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
fun formatBeerPriceWithSymbol(
    rawPrice: String,
    beerCurrency: String
): String {
    val symbol = when (beerCurrency.uppercase()) {
        "EUR" -> "€"
        "USD" -> "$"
        else  -> ""
    }
    return "$symbol$rawPrice"
}