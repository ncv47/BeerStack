package com.example.beerstack

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.beerstack.data.remote.SupabaseCollectionRepository
import com.example.beerstack.data.remote.UserBeerDto
import com.example.beerstack.ui.theme.BeerGradient
import com.example.beerstack.ui.theme.BeerStackTheme

class SecondActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the logged-in user ID passed from ThirdActivity
        val userId = intent.getIntExtra("USER_ID", -1)
        val username = intent.getStringExtra("USER_NAME") ?: "Unknown"

        //Use Supabase (Own API)
        val supabaseRepo = SupabaseCollectionRepository()

        setContent {
            BeerStackTheme(dynamicColor = false) {
                var items by remember { mutableStateOf<List<UserBeerDto>>(emptyList()) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BeerGradient)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Show the logged-in user
                        Text(
                            text = "Logged in User: $username",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Item list
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            items(items) { beer ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable {
                                            val intent = Intent(
                                                this@SecondActivity,
                                                EighthActivity::class.java
                                            ).apply {
                                                putExtra("beer_entry", beer)
                                            }
                                            startActivity(intent)
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Stock image on the left
                                    beer.imageurl?.let { url ->
                                        AsyncImage(
                                            model = url,
                                            contentDescription = "Beer image",
                                            modifier = Modifier
                                                .size(72.dp)
                                                .padding(end = 12.dp),
                                            contentScale = ContentScale.Crop,
                                            placeholder = painterResource(R.drawable.beerpicture_placeholder),
                                            error = painterResource(R.drawable.beerpicture_placeholder)
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    ) {
                                        Text(text = "Name: ${beer.name}")
                                        Text(text = "My Rating: %.1f".format(beer.myrating))
                                        Text(text = "Average: %.1f".format(beer.apiaverage))
                                    }
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }

                        // Collect the Flow and filter items by ownerId
                        LaunchedEffect(userId) {
                            if (userId != -1) {
                                items = supabaseRepo.getCollection(userId)
                            }
                        }

                        BottomBar(
                            userId = userId,
                            username = username,
                            currentScreenIsHome = false,
                            currentScreenIsStack = true
                        )
                    }
                }
            }
        }
    }
}
