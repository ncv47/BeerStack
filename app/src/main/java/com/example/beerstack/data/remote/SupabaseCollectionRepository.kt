package com.example.beerstack.data.remote

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class UserBeerCount(
    val userId: Int,
    val count: Int
)

class SupabaseCollectionRepository {

    suspend fun getLeaderboard(): List<UserBeerCount> = withContext(Dispatchers.IO) {
        // Takes all the beers from the BeerCollection
        val allBeers = client.from("BeerCollection")
            .select()
            // Takes the JSON and decodes it
            .decodeList<UserBeerDto>()

        allBeers
            // Goes over every pair and groups them by userId
            .groupBy { it.userid }
            .map { (userId, beers) ->
                UserBeerCount(
                    userId = userId,
                    count = beers.size
                )
            }
            .sortedByDescending { it.count }
    }

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