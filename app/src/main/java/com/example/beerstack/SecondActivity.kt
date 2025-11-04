package com.example.beerstack

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.beerstack.data.AppDataContainer
import com.example.beerstack.data.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

// Second page for database testing
class SecondActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize your repository
        val repository = AppDataContainer(this).itemsRepository

        // Insert a sample item so the database is created
        CoroutineScope(Dispatchers.IO).launch {
            val currentItems = repository.getAllItemsStream().firstOrNull() ?: emptyList()
            if (currentItems.isEmpty()) {
                repository.insertItem(
                    Item(
                        beername = "Sample Beer",
                        beerprice = 5,
                        beerrating = 4
                    )
                )
            }
        }
    }
}
