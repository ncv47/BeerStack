package com.example.beerstack.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beerstack.network.SampleApi
import kotlinx.coroutines.launch
import com.example.beerstack.model.Beer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import com.example.beerstack.model.Currency

// Holds and manages beer list & error state for UI
class BeerViewModel : ViewModel() {
    // Start with empty list
    var beerList by mutableStateOf<List<Beer>>(emptyList())
    // Errorhandeling while loading
    var error by mutableStateOf<String?>(null)

    // Currency state
    var currency by mutableStateOf(Currency.USD)
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
                // optional: handle error
            }
        }
    }

    //Try request the beers from the api
    fun getBeers(query: String = "") {
        viewModelScope.launch {
            try {
                //If ok, update beer list and clear any error
                val fetchedBeers = SampleApi.retrofitService.getBeers()
                beerList = filterValidBeers(fetchedBeers)
                    .filter { it.name.contains(query, ignoreCase = true) } //To filter out for the search function
                error = null
            } catch (e: Exception) { //Catch the error
                // if not ok, reset list and show error
                beerList = emptyList()
                error = "Failed to load beers: ${e.message}"
            }
        }
        fun toggleCurrency() {
            currency = if (currency == Currency.USD) Currency.EUR else Currency.USD
        }

        fun refreshRate() {
            viewModelScope.launch {
                try {
                    eurPerUsd = fetchEurPerUsd()
                } catch (e: Exception) {
                    // Optional: reuse error state, or ignore silently
                    // error = "Failed to load FX rate: ${e.message}"
                }
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

    fun getBeerById(id: Int) {
        viewModelScope.launch {
            try {
                val beer = SampleApi.retrofitService.getBeerById(id)
                lastAddedBeerName = beer.name
                lastAddedBeerError = null
            } catch (e: Exception) {
                lastAddedBeerName = null
                lastAddedBeerError = "Failed to fetch beer: ${e.message}"
            }
        }
    }

    // Call this to clear popup after dismiss
    fun clearLastBeerInfo() {
        lastAddedBeerName = null
        lastAddedBeerError = null
    }
}

// Fetch EUR/USD rate from currency API
private suspend fun fetchEurPerUsd(): Double =
    withContext(Dispatchers.IO) {
        val url = URL(
            "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json"
        )

        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5000
            readTimeout = 5000
        }

        connection.inputStream.bufferedReader().use { reader ->
            val json = reader.readText()
            // JSON looks like: { "date": "...", "eur": { "usd": 1.08, ... } }
            val root = JSONObject(json)
            val eur = root.getJSONObject("eur")
            val eurToUsd = eur.getDouble("usd")   // 1 EUR -> X USD
            1.0 / eurToUsd                        // store 1 USD -> X EUR
        }
    }