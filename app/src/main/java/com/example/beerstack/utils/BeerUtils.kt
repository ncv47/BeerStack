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

//Temp Helper for Currency Converter (to be optimized or put in new helper directory)
fun formatBeerPrice(
    rawPrice: String?,
    currency: Currency,
    eurPerUsd: Double
): String {
    val valueUsd = rawPrice
        ?.replace("$", "")
        ?.replace("€", "")
        ?.toDoubleOrNull() ?: return "N/A"

    val value = when (currency) {
        Currency.USD -> valueUsd
        Currency.EUR -> valueUsd * eurPerUsd
    }

    val symbol = if (currency == Currency.USD) "$" else "€"
    return "%s%.2f".format(symbol, value)
}