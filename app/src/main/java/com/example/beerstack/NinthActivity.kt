package com.example.beerstack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.beerstack.data.AppDatabase
import com.example.beerstack.data.UserDB.OfflineUsersRepository
import com.example.beerstack.data.UserDB.UsersRepository
import com.example.beerstack.data.remote.SupabaseCollectionRepository
import com.example.beerstack.data.remote.UserBeerCount
import com.example.beerstack.ui.theme.BeerGradient
import com.example.beerstack.ui.theme.BeerStackTheme
import kotlinx.coroutines.flow.first

class NinthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getIntExtra("USER_ID", -1)
        val username = intent.getStringExtra("USER_NAME") ?: "Unknown"

        // Talks to the API
        val supabaseRepo = SupabaseCollectionRepository()

        // Creates a room instance
        val db = AppDatabase.getDatabase(this)
        // Updates the table locally
        val usersRepository: UsersRepository = OfflineUsersRepository(db.userDao())

        enableEdgeToEdge()
        setContent {
            BeerStackTheme(dynamicColor = false) {

                var localUserMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }

                LaunchedEffect(Unit) {
                    // Loads all the users
                    val allUsers = usersRepository.getAllUsers().first()
                    localUserMap = allUsers.associate { it.userid to it.userName }
                }

                LeaderboardScreen(
                    userId = userId,
                    username = username,
                    supabaseRepo = supabaseRepo,
                    localUserMap = localUserMap
                )
            }
        }
    }
}

@Composable
fun LeaderboardScreen(
    userId: Int,
    username: String,
    supabaseRepo: SupabaseCollectionRepository,
    localUserMap: Map<Int, String>
) {
    var rows by remember { mutableStateOf<List<UserBeerCount>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(localUserMap) {
        // Only loads the body once all users are gotten
        if (localUserMap.isEmpty()) return@LaunchedEffect

        try {
            isLoading = true
            error = null

            val counted = supabaseRepo.getLeaderboard()
            val countMap = counted.associateBy { it.userId }

            val allRows = localUserMap.keys.map { uid ->
                val existing = countMap[uid]
                UserBeerCount(
                    userId = uid,
                    // Shows all users even if they don't have any beers yet
                    count = existing?.count ?: 0
                )
            }
            // Orders the users
            rows = allRows.sortedWith(
                compareByDescending<UserBeerCount> { it.count }
                    .thenBy { localUserMap[it.userId] ?: "" }
            )
        } catch (e: Exception) {
            error = "Failed to load leaderboard: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BeerGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // Reuse your styled TopBar
                TopBar(userId = userId, username = username)
            },
            bottomBar = {
                // Reuse your styled BottomBar with Leaderboard selected
                BottomBar(
                    userId = userId,
                    username = username,
                    currentScreenIsHome = false,
                    currentScreenIsStack = false,
                    currentScreenIsLeaderboard = true
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Leaderboard",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when {
                    isLoading -> {
                        // Fancy stuff
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    error != null -> {
                        Text(text = error ?: "Unknown error")
                    }
                    rows.isEmpty() -> {
                        Text(text = "No users found.")
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(rows) { entry ->
                                LeaderboardRow(
                                    entry = entry,
                                    isCurrentUser = entry.userId == userId,
                                    localUserMap = localUserMap
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardRow(
    entry: UserBeerCount,
    isCurrentUser: Boolean,
    localUserMap: Map<Int, String>
) {
    val name = localUserMap[entry.userId] ?: "User ${entry.userId}"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isCurrentUser)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isCurrentUser)
                        androidx.compose.ui.text.font.FontWeight.Bold
                    else
                        androidx.compose.ui.text.font.FontWeight.Normal
                )
                if (isCurrentUser) {
                    Text(
                        text = "That's you!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = "${entry.count} ${if (entry.count == 1) "beer" else "beers"}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
