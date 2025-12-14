package com.example.beerstack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.beerstack.data.AppDatabase
import com.example.beerstack.data.UserDB.OfflineUsersRepository
import com.example.beerstack.data.UserDB.UsersRepository
import com.example.beerstack.data.remote.SupabaseCollectionRepository
import com.example.beerstack.data.remote.UserBeerCount
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

                LeaderboardScreenBare(
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
fun LeaderboardScreenBare(
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

    Scaffold(
        topBar = {
            Text(
                text = "Leaderboard ($username)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
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
                    LazyColumn {
                        items(rows) { entry ->
                            LeaderboardRowBare(
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

@Composable
fun LeaderboardRowBare(
    entry: UserBeerCount,
    isCurrentUser: Boolean,
    localUserMap: Map<Int, String>
) {
    val name = localUserMap[entry.userId] ?: "User ${entry.userId}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.bodyLarge)
            if (isCurrentUser) {
                Text(text = "(you)", style = MaterialTheme.typography.bodySmall)
            }
        }
        Text(
            text = "${entry.count} ${if (entry.count == 1) "beer" else "beers"}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
