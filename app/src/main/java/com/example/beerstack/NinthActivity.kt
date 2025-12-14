package com.example.beerstack

import android.os.Bundle
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.beerstack.data.AppDatabase
import com.example.beerstack.data.UserDB.OfflineUsersRepository
import com.example.beerstack.data.UserDB.UsersRepository
import com.example.beerstack.data.remote.SupabaseCollectionRepository
import com.example.beerstack.data.remote.UserBeerCount
import com.example.beerstack.data.remote.UserBeerDto
import com.example.beerstack.ui.theme.BeerGradient
import com.example.beerstack.ui.theme.BeerStackTheme
import kotlinx.coroutines.flow.first
import java.util.Calendar
import kotlin.io.path.Path
import kotlin.io.path.moveTo

//---LEADERBOARD---

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
    var userBeers by remember { mutableStateOf<List<UserBeerDto>>(emptyList()) }
    val drinksPerDay = userBeers.drinksLast7Days()

    LaunchedEffect(userId) {
        try {
            userBeers = supabaseRepo.getCollection(userId)
        } catch (e: Exception) {
            // handle error if needed
        }
    }

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
                    count = existing?.count ?: 0,
                    lastBeerDate = existing?.lastBeerDate
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
                            modifier = Modifier.weight(1f),
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

                        Spacer(modifier = Modifier.height(16.dp))

                        DrinksGraphCard(
                            drinksPerDay = drinksPerDay,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                }
            }
        }
    }
}
fun List<UserBeerDto>.drinksLast7Days(): List<Int> {
    val today = Calendar.getInstance()
    val counts = MutableList(7) { 0 }

    // For each beer, check which day (0 = 6 days ago, 6 = today)
    forEach { beer ->
        val beerDate = beer.date?.let {
            try {
                // Parse ISO date (yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss)
                val parts = it.split("T")[0].split("-").map { p -> p.toInt() }
                val cal = Calendar.getInstance()
                cal.set(parts[0], parts[1] - 1, parts[2], 0, 0, 0)
                cal
            } catch (e: Exception) { null }
        } ?: return@forEach

        val diff = ((today.timeInMillis - beerDate.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
        if (diff in 0..6) {
            counts[6 - diff] += 1 // index 6 = today
        }
    }

    return counts
}

@Composable
fun LeaderboardRow(
    entry: UserBeerCount,
    isCurrentUser: Boolean,
    localUserMap: Map<Int, String>
) {
    val name = localUserMap[entry.userId] ?: "User ${entry.userId}"

    Card(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
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


@Composable
fun DrinksGraphCard(
    drinksPerDay: List<Int>, // must have exactly 7 items
    modifier: Modifier = Modifier
) {
    require(drinksPerDay.size == 7) { "drinksPerDay must have 7 items" }

    val maxDrinks = drinksPerDay.maxOrNull() ?: 0
    val circleRadius = 6.dp
    val lineColor = Color(0xFF4A90E2)
    val fillColor = lineColor.copy(alpha = 0.3f)
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Card(
        modifier = modifier.fillMaxWidth().height(260.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("DRINKS >", style = MaterialTheme.typography.titleSmall, color = Color.White)
                Text("SEE ALL", style = MaterialTheme.typography.titleSmall, color = Color(0xFF5DA9FF))
            }

            Spacer(Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val spacing = size.width / (drinksPerDay.size - 1)
                    val baseline = size.height * 0.75f
                    val points = drinksPerDay.mapIndexed { i, count ->
                        Offset(i * spacing, baseline - if (maxDrinks > 0) (count / maxDrinks.toFloat()) * baseline * 0.7f else 0f)
                    }

                    // Draw line and filled area
                    for (i in 0 until points.lastIndex) drawLine(lineColor, points[i], points[i + 1], 3f, StrokeCap.Round)
                    drawPath(Path().apply { moveTo(points.first().x, baseline); points.forEach { lineTo(it.x, it.y) }; lineTo(points.last().x, baseline); close() }, fillColor)

                    // Draw circles
                    val pxRadius = circleRadius.toPx()
                    points.forEach { drawCircle(lineColor, pxRadius, it) }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                days.forEach { Text(it, style = MaterialTheme.typography.bodySmall, color = Color.White) }
            }
        }
    }
}
