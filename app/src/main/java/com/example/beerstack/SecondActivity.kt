package com.example.beerstack

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.beerstack.components.UserBeerGroupCard
import com.example.beerstack.components.UserBeerItemCard
import com.example.beerstack.data.remote.SupabaseCollectionRepository
import com.example.beerstack.data.remote.UserBeerDto
import com.example.beerstack.ui.theme.BeerGradient
import com.example.beerstack.ui.theme.BeerStackTheme

//---STACK SCREEN (COLLECTION)---

class SecondActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the logged-in user ID passed from ThirdActivity
        val userId = intent.getIntExtra("USER_ID", -1)
        val username = intent.getStringExtra("USER_NAME") ?: "Unknown"

        //Use Supabase (Own API)
        val supabaseRepo = SupabaseCollectionRepository()

        setContent {
            BeerStackTheme() {
                var items by remember { mutableStateOf<List<UserBeerDto>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }
                var error by remember { mutableStateOf<String?>(null) }


                // Collect the collection and filter items by ownerId
                LaunchedEffect(userId) {
                    if (userId == -1) return@LaunchedEffect
                    try {
                        isLoading = true
                        error = null
                        items = supabaseRepo.getCollection(userId)
                    } catch (e: Exception) {
                        error = "Failed to load collection: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }

                val groupedItems: List<Pair<String, List<UserBeerDto>>> by remember(items) {
                    mutableStateOf(items.groupBy { it.name }.toList())
                } // Needed to group all items by name and keep them in this list


                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BeerGradient)
                ) {
                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            TopBar(userId = userId, username = username)
                        },
                        bottomBar = {
                            BottomBar(
                                userId = userId,
                                username = username,
                                currentScreenIsHome = false,
                                currentScreenIsStack = true,
                                currentScreenIsLeaderboard = false
                            )
                        }
                    ) { innerPadding ->

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center   // center child
                        ) {
                            when {
                                isLoading -> {
                                    CircularProgressIndicator()
                                }
                                error != null -> {
                                    Text(
                                        text = error ?: "Unknown error",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                items.isEmpty() -> {
                                    Text(text = "Your stack is empty.")
                                }
                                else -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

                                        Text(
                                            text = "Your Stack",
                                            style = MaterialTheme.typography.headlineMedium,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )

                                        // Item list
                                        LazyColumn(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth()
                                                .padding(top = 48.dp)
                                        ) {

                                            // 1. Grouped, expandable list
                                            items(groupedItems) { (name, beersWithSameName) ->

                                                //If there is only 1 item it will not show another dropdownlist for just that one item
                                                if (beersWithSameName.size == 1) {
                                                    val beer = beersWithSameName.first()

                                                    //Use an itemcard inside UserBeerCards so you dont have this long code in here
                                                    UserBeerItemCard(
                                                        beer = beer,
                                                        onClick = {
                                                            val intent = Intent(
                                                                this@SecondActivity,
                                                                EighthActivity::class.java
                                                            ).apply {
                                                                putExtra("beer_entry", beer)
                                                            }
                                                            startActivity(intent)
                                                        }
                                                    )
                                                } else {

                                                    //Same thing here uses a card inside UserBeerCards so you dont have the long code here
                                                    UserBeerGroupCard(
                                                        name = name,
                                                        beersWithSameName = beersWithSameName,
                                                        onBeerClick = { beer ->
                                                            val intent = Intent(
                                                                this@SecondActivity,
                                                                EighthActivity::class.java
                                                            ).apply { putExtra("beer_entry", beer) }
                                                            startActivity(intent)
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
