package com.example.beerstack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.beerstack.data.BeerDB.AppDataContainer
import com.example.beerstack.data.BeerDB.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


// Second page for database testing
class SecondActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize your repository
        val repository = AppDataContainer(this).itemsRepository
        setContent {
            MaterialTheme {
                var items by remember { mutableStateOf<List<Item>>(emptyList()) }

                // Collect the Flow from Room
                LaunchedEffect(Unit) {
                    repository.getAllItemsStream().collectLatest {
                        items = it
                    }
                }
                Scaffold(

                ) { padding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp)
                    ) {
                        items(items) { beer ->
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Name: ${beer.beername}")
                                Text(text = "Price: ${beer.beerprice}")
                                Text(text = "Rating: ${beer.beerrating}")
                            }
                        }
                    }
                }
            }
        }
        // Insert a sample item so the database is created
        CoroutineScope(Dispatchers.IO).launch {
            val currentItems = repository.getAllItemsStream().firstOrNull() ?: emptyList()
            if (currentItems.isEmpty()) {
                val beers = listOf(
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                    Item(beername = "Sample Beer", beerprice = 8, beerrating = 4),
                )
                beers.forEach { repository.insertItem(it) }
            }
        }
    }
}
