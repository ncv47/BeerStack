package com.example.beerstack.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beerstack.network.SampleApi
import kotlinx.coroutines.launch
import com.example.beerstack.model.Beer

class BeerViewModel : ViewModel() {
    var beerList by mutableStateOf<List<Beer>>(emptyList())
    var error by mutableStateOf<String?>(null)

    fun getBeers() {
        viewModelScope.launch {
            try {
                beerList = SampleApi.retrofitService.getBeers()
                error = null
            } catch (e: Exception) {
                beerList = emptyList()
                error = "Failed to load beers: ${e.message}"
            }
        }
    }
}
