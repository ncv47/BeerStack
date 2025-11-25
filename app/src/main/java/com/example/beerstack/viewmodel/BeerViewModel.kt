package com.example.beerstack.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beerstack.network.SampleApi
import kotlinx.coroutines.launch
import com.example.beerstack.model.Beer

// Holds and manages beer list & error state for UI
class BeerViewModel : ViewModel() {
    // Start with empty list
    var beerList by mutableStateOf<List<Beer>>(emptyList())
    // Errorhandeling while loading
    var error by mutableStateOf<String?>(null)

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
