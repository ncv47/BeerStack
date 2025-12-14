package com.example.beerstack

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.beerstack.data.remote.SupabaseCollectionRepository
import com.example.beerstack.ui.theme.BeerStackTheme
import com.example.beerstack.ui.theme.BeerGradient

class FifthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username = intent.getStringExtra("USER_NAME") ?: "User"
        val userId = intent.getIntExtra("USER_ID", -1)

        // Talks to the API for the user's collection
        val supabaseRepo = SupabaseCollectionRepository()

        setContent {
            BeerStackTheme(dynamicColor = false) {
                val context = LocalContext.current

                ProfileScreen(
                    username = username,
                    userId = userId,
                    supabaseRepo = supabaseRepo,
                    onLogout = {
                        val intent = Intent(context, ThirdActivity::class.java)
                        context.startActivity(intent)
                    },
                    onBack = { finish() }  // Closes this screen and returns to the previous one
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(
    username: String,
    userId: Int,
    supabaseRepo: SupabaseCollectionRepository,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    var favoriteBeerName by remember { mutableStateOf("None") }
    var beersReviewed by remember { mutableIntStateOf(0) }

    // Load the user's collection and compute favorite beer = beer you have the most of
    LaunchedEffect(userId) {
        if (userId == -1) return@LaunchedEffect
            // Get all beers in this user's collection
            val collection = supabaseRepo.getCollection(userId)  // List<UserBeerDto>

            // Number of beers reviewed = number of entries
            beersReviewed = collection.size

            // Group by beer name and pick the one with the highest count
            val favorite = collection
                .groupBy { it.name }
                .maxByOrNull { (_, beers) -> beers.size }

            favoriteBeerName = favorite?.key ?: "None"

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BeerGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                ProfileTopBar(onBack = onBack)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileHeader(username = username, userId = userId)

                Spacer(modifier = Modifier.height(28.dp))
                ProfileInfoRow("Username", username)
                ProfileInfoRow("User ID", userId.toString())
                ProfileInfoRow("Member Since", "2025")
                ProfileInfoRow("Favorite Beer", favoriteBeerName)
                ProfileInfoRow("Beers Reviewed", beersReviewed.toString())

                Spacer(modifier = Modifier.height(40.dp))

                LogoutSection(onLogout = onLogout)
            }
        }
    }
}

@Composable
fun ProfileTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row( // This extra row is so that the arrow and the return text acts like on button
            modifier = Modifier
                .clip(RoundedCornerShape(50)) // Makes it so that when you click it the shadow doesn't look like one big block but is an actual nice rounded shape that just fits
                .clickable { onBack() } // Makes it so that the arrow and the text "return" are clickable to go back
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Return",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ProfileHeader(username: String, userId: Int) {
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

    Text(
        text = username,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = "ID: $userId",
        fontSize = 14.sp,
        color = Color.Gray
    )
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Text(value, color = Color.Gray)
    }
}

@Composable
fun LogoutSection(onLogout: () -> Unit) {
    FilledTonalButton(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth(0.7f)
    ) {
        Text("Log Out")
    }
}
