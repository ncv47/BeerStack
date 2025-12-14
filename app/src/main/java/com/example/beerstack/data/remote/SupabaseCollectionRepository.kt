package com.example.beerstack.data.remote

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Represents a user's position in the leaderboard
data class UserBeerCount(
    val userId: Int,
    val count: Int,
    val lastBeerDate: String? // ISO string from Supabase
)


// Handles all interactions with Supabase for the BeerCollection and Beers tables
class SupabaseCollectionRepository {

    // Fetch all beers from the BeerCollection table, group by user, and count beers per user
    // Returns a list of UserBeerCount objects sorted descending by count
    suspend fun getLeaderboard(): List<UserBeerCount> = withContext(Dispatchers.IO) {
        val allBeers = client.from("BeerCollection")
            .select()
            .decodeList<UserBeerDto>()

        allBeers
            .groupBy { it.userid }
            .map { (userId, beers) ->

                // Find the most recent date for this user
                val latestDate = beers
                    .mapNotNull { it.date }
                    .maxOrNull() // works if ISO-8601 (yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss)

                UserBeerCount(
                    userId = userId,
                    count = beers.size,
                    lastBeerDate = latestDate
                )
            }
            .sortedWith(
                compareByDescending<UserBeerCount> { it.count }
                    .thenByDescending { it.lastBeerDate }
            )
    }


    // Reference to the Supabase client
    private val client = supabaseClient

    // Fetch all beers collected by a specific user
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

    // Add a beer entry to the BeerCollection table for a specific user
    suspend fun addBeerToCollection(item: UserBeerDto) =
        withContext(Dispatchers.IO) {
            client.from("BeerCollection")
                .insert(item)
        }

    // Add a new beer entry to the global Beers table
    suspend fun addBeerToBeerList(beer: SupabaseBeerDto) =
        withContext(Dispatchers.IO) {
            client.from("Beers")
                .insert(beer)
        }}