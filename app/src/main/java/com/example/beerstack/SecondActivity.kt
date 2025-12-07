package com.example.beerstack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.beerstack.data.BeerDB.AppDataContainer
import com.example.beerstack.data.BeerDB.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SecondActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AppDataContainer(this).itemsRepository

        // Get the logged-in user ID passed from ThirdActivity
        val userId = intent.getIntExtra("USER_ID", -1)
        setContent {
            MaterialTheme {
                var items by remember { mutableStateOf<List<Item>>(emptyList()) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Show the logged-in user ID
                    Text(
                        text = "Logged in User ID: $userId",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Item list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(items) { beer ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text(text = "Name: ${beer.beername}")
                                Text(text = "Price: ${beer.beerprice}")
                                Text(text = "Rating: ${beer.beerrating}")
                                Text(text = "Average: ${beer.beeraverage}")
                            }
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }

                // Collect the Flow and filter items by ownerId
                LaunchedEffect(Unit) {
                    if (userId != -1) {
                        repository.getItemsByOwner(userId).collectLatest { filteredItems ->
                            items = filteredItems
                        }
                    }
                }
            }
        }

        // Optional: insert sample items for testing (only if DB is empty)
        lifecycleScope.launch(Dispatchers.IO) {
            val currentItems = repository.getItemsByOwner(userId).firstOrNull() ?: emptyList()
            if (currentItems.isEmpty() && userId != -1) {
                val beers = listOf(
                    Item(
                        beername = "userid1",
                        beerprice = 8,
                        beerimage = "",
                        beerrating = 4,
                        beeraverage = 3.4,
                        ownerId = 1
                    ),
                    Item(
                        beername = "userid2",
                        beerprice = 10,
                        beerimage = "",
                        beerrating = 5,
                        beeraverage = 4.0,
                        ownerId = 2
                    ),
                    Item(
                        beername = "userid3",
                        beerprice = 10,
                        beerimage = "",
                        beerrating = 5,
                        beeraverage = 4.0,
                        ownerId = 3
                    ),
                    Item(
                        beername = "userid4",
                        beerprice = 10,
                        beerimage = "",
                        beerrating = 5,
                        beeraverage = 4.0,
                        ownerId = 4
                    )

                )
                beers.forEach { repository.insertItem(it) }
            }
        }
    }
}
