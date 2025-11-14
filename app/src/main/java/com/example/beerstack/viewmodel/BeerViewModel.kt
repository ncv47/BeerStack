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
    fun getBeers() {
        viewModelScope.launch {
            try {
                //If ok, update beer list and clear any error
                beerList = SampleApi.retrofitService.getBeers()
                error = null
            } catch (e: Exception) {
                // if not ok, rest list and show error
                beerList = emptyList()
                error = "Failed to load beers: ${e.message}"
            }
        }
    }
}
