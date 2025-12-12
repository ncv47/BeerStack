package com.example.beerstack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beerstack.data.BeerDB.AppDataContainer
import kotlinx.coroutines.flow.collectLatest

class FifthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username = intent.getStringExtra("USER_NAME") ?: "User"
        val userId = intent.getIntExtra("USER_ID", -1)
        val repository = AppDataContainer(this).itemsRepository

        setContent {
            MaterialTheme {
                val context = LocalContext.current

                // State for favorite beer and beers reviewed
                var favoriteBeerName by remember { mutableStateOf("None") }
                var beersReviewed by remember { mutableStateOf(0) }

                // Collect the user's beers from the database
                LaunchedEffect(userId) {
                    if (userId != -1) {
                        repository.getItemsByOwner(userId).collectLatest { filteredItems ->
                            val favoriteBeer = filteredItems.maxByOrNull { it.beerrating }
                            favoriteBeerName = favoriteBeer?.beername ?: "None"
                            beersReviewed = filteredItems.size
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Profile Initial Circle
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = username.take(1).uppercase(),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Username
                    Text(
                        text = username,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // ID line
                    Text(
                        text = "ID: $userId",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Info Rows
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Username", fontWeight = FontWeight.SemiBold)
                        Text(username, color = Color.Gray)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("User ID", fontWeight = FontWeight.SemiBold)
                        Text(userId.toString(), color = Color.Gray)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Member Since", fontWeight = FontWeight.SemiBold)
                        Text("2025", color = Color.Gray)
                    }

                    // Favorite Beer row — dynamic from database
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Favorite Beer", fontWeight = FontWeight.SemiBold)
                        Text(favoriteBeerName, color = Color.Gray)
                    }

                    // Beers Reviewed row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Beers Reviewed", fontWeight = FontWeight.SemiBold)
                        Text(beersReviewed.toString(), color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Logout button
                    FilledTonalButton(
                        onClick = {
                            val intent = Intent(context, ThirdActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text("Log Out")
                    }
                }
            }
        }
    }
}
