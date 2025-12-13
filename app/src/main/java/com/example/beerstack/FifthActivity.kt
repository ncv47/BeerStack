package com.example.beerstack

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beerstack.ui.theme.BeerStackTheme
import com.example.beerstack.ui.theme.BeerGradient

class FifthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username = intent.getStringExtra("USER_NAME") ?: "User"
        val userId = intent.getIntExtra("USER_ID", -1)

        setContent {
            BeerStackTheme(dynamicColor = false) {
                val context = LocalContext.current

                ProfileScreen(
                    username = username,
                    userId = userId,
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
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    var favoriteBeerName by remember { mutableStateOf("None") }
    var beersReviewed by remember { mutableStateOf(0) }


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
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Return",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
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