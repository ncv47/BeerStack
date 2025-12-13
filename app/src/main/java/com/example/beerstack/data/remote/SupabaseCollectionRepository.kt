package com.example.beerstack.data.remote

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SupabaseCollectionRepository {

    private val client = supabaseClient

    suspend fun getCollection(userId: Int): List<UserBeerDto> =
        withContext(Dispatchers.IO) {
            client.from("BeerCollection")
                .select {
                    filter {
                        UserBeerDto::userid eq userId
                    }
                }
                .decodeList<UserBeerDto>()
        }

    suspend fun addBeerToCollection(item: UserBeerDto) =
        withContext(Dispatchers.IO) {
            client.from("BeerCollection")
                .insert(item)
        }

    suspend fun addBeerToBeerList(beer: BeerDto) =
        withContext(Dispatchers.IO) {
            client.from("Beers")
                .insert(beer)
        }}