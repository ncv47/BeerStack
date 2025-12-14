package com.example.beerstack.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
    // Error handling while loading
    var error by mutableStateOf<String?>(null)

    // Loading flag for API calls
    var isLoading by mutableStateOf(false)
        private set

    // Currency state (start with euro)
    var currency by mutableStateOf(Currency.EUR)
        private set

    // How many EUR for 1 USD (used for conversion)
    var eurPerUsd by mutableDoubleStateOf(1.0)
        private set

    // Toggle between EUR and USD currency
    fun toggleCurrency() {
        currency = if (currency == Currency.USD) Currency.EUR else Currency.USD
    }

    // Refresh currency conversion rate from the API
    fun refreshConversionRate() {
        viewModelScope.launch {
            try {
                eurPerUsd = fetchEurPerUsd() // Calls API and updates rate
            } catch (e: Exception) {
                println("ERROR: ${e.message}")
            }
        }
    }

    fun getBeers(query: String = "") {
        viewModelScope.launch {
            try {
                isLoading = true
                // 1. Fetch from Supabase (list of BeerDto)
                //If ok, update beer list and clear any error
                val dtoList = SupabaseApi.retrofitService.getBeers()

                // 2. Map to Beer model
                val fetchedBeers = dtoList.map { it.toBeer() }

                // 3. Filter and search
                beerList = fetchedBeers.filter { it.name.contains(query, ignoreCase = true) }

                error = null
            } catch (e: Exception) { //Catch the error
                // if not ok, reset list and show error
                beerList = emptyList()
                error = "Failed to load beers: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Fetch EUR/USD rate from currency API (See CurrencyApiService.kt)
    private suspend fun fetchEurPerUsd(): Double {
        // 1 EUR -> X USD
        val response = CurrencyApi.retrofitService.getEurRates()
        val eurToUsd = response.eur.usd
        return 1.0 / eurToUsd      // store 1 USD -> X EUR (same logic as before)
    }
}
