package com.example.beerstack.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.beerstack.model.Beer
import com.example.beerstack.model.Currency
import com.example.beerstack.network.CurrencyApi
import com.example.beerstack.network.SupabaseApi
import com.example.beerstack.model.toBeer

// Holds and manages beer list & error state for UI
class BeerViewModel : ViewModel() {
    // Start with empty list
    var beerList by mutableStateOf<List<Beer>>(emptyList())
    // Errorhandeling while loading
    var error by mutableStateOf<String?>(null)

    // Currency state (start with euro)
    var currency by mutableStateOf(Currency.EUR)
        private set

    // How many EUR for 1 USD (used for conversion)
    var eurPerUsd by mutableStateOf(1.0)
        private set

    fun toggleCurrency() {
        currency = if (currency == Currency.USD) Currency.EUR else Currency.USD
    }

    fun refreshRate() {
        viewModelScope.launch {
            try {
                eurPerUsd = fetchEurPerUsd()
            } catch (e: Exception) {
                println("ERROR: ${e.message}")
            }
        }
    }

    fun getBeers(query: String = "") {
        viewModelScope.launch {
            try {
                // 1. Fetch from Supabase (list of BeerDto)
                //If ok, update beer list and clear any error
                val dtoList = SupabaseApi.retrofitService.getBeers()

                // 2. Map to Beer model
                val fetchedBeers = dtoList.map { it.toBeer() }

                // 3. Filter and search
                beerList = filterValidBeers(fetchedBeers)
                    .filter { it.name.contains(query, ignoreCase = true) }

                error = null
            } catch (e: Exception) { //Catch the error
                // if not ok, reset list and show error
                beerList = emptyList()
                error = "Failed to load beers: ${e.message}"
            }
        }
    }

    // Filter out test data or invalid beers
    private fun filterValidBeers(beers: List<Beer>): List<Beer> =
        // THere is some test/invalid date in the API, to not show this filter them like so
        // ALso filter out beers with no price and beer names shouldn't have { in there name
        beers.filter { beer ->
            !beer.name.contains("{") &&
                    !beer.name.contains("random") &&
                    beer.price != null &&
                    beer.price != "{{price}}" &&
                    !beer.name.equals("Hi", ignoreCase = true)
        }

    //All this is for the collectoin, specific beer fetch from api (2nd request)
    var lastAddedBeerName by mutableStateOf<String?>(null)
    var lastAddedBeerError by mutableStateOf<String?>(null)

    // Call this to clear popup after dismiss
    fun clearLastBeerInfo() {
        lastAddedBeerName = null
        lastAddedBeerError = null
    }

    // Fetch EUR/USD rate from currency API (See CurrencyApiService.kt)
    private suspend fun fetchEurPerUsd(): Double {
        // 1 EUR -> X USD
        val response = CurrencyApi.retrofitService.getEurRates()
        val eurToUsd = response.eur.usd
        return 1.0 / eurToUsd      // store 1 USD -> X EUR (same logic as before)
    }
}

