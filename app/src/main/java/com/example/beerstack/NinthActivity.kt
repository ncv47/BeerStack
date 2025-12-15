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
import androidx.compose.runtime.*
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
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
            BeerStackTheme {

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
    val drinksPerDay = userBeers.drinksThisWeek()



    LaunchedEffect(userId) {
        try {
            userBeers = supabaseRepo.getCollection(userId)
        } catch (_: Exception) {
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
                // Reusing the topbar not generaltopbar because thats for returning to the page
                TopBar(userId = userId, username = username)
            },
            bottomBar = {
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
                        MaterialTheme.colorScheme.onSecondary,
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

// Returns a Calendar instance representing the start of the current week (Monday 00:00:00.000)
fun getStartOfWeek(): Calendar {
    val cal = Calendar.getInstance() // Get current date and time
    cal.firstDayOfWeek = Calendar.MONDAY // Set Monday as the first day of the week
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) // Move calendar to this week's Monday
    cal.set(Calendar.HOUR_OF_DAY, 0) // Reset hour to 0
    cal.set(Calendar.MINUTE, 0)      // Reset minute to 0
    cal.set(Calendar.SECOND, 0)      // Reset second to 0
    cal.set(Calendar.MILLISECOND, 0) // Reset millisecond to 0
    return cal // Return the calendar pointing to Monday 00:00
}

// Extension function on a list of UserBeerDto to calculate number of drinks for each day of the current week
fun List<UserBeerDto>.drinksThisWeek(): List<Int> {
    val start = getStartOfWeek() // Get start of the week (Monday 00:00)
    val counts = MutableList(7) { 0 } // Initialize list to hold counts for 7 days (Monday = 0, Sunday = 6)

    forEach { beer ->
        // Parse the date string of the beer (assumes ISO format like yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss)
        beer.date?.split("T")?.get(0)?.split("-")?.map { it.toInt() }?.let { (y, m, d) ->
            // Create a Calendar for the beer's date at 00:00
            Calendar.getInstance().apply {
                set(y, m-1, d, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }
            // Only consider beers that are not before the start of the week
            ?.takeIf { !it.before(start) }
            ?.let {
                // Calculate difference in days from start of the week
                val diff = ((it.timeInMillis - start.timeInMillis) / (24 * 60 * 60 * 1000)).toInt()
                // Increment count for the correct day if within 0..6
                if (diff in 0..6) counts[diff]++
            }
    }

    return counts // Return list of counts: counts[0] = Monday, counts[6] = Sunday
}

// Composable to display a graph of drinks per day
@Composable
fun DrinksGraphCard(drinksPerDay: List<Int>, modifier: Modifier = Modifier) {
    require(drinksPerDay.size == 7) { "drinksPerDay must contain exactly 7 items" } // Sanity check

    val max = drinksPerDay.maxOrNull() ?: 0 // Find the maximum drinks in the week
    val lineColor = Color(0xFF4A90E2)       // Line and circle color
    val fillColor = lineColor.copy(alpha = 0.3f) // Fill under the curve
    val days = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun") // Day labels

    // Card container for the graph
    Card(
        modifier.fillMaxWidth().height(260.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            // Box for the line graph
            Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val spacing = size.width / (drinksPerDay.size - 1) // Horizontal spacing between points
                    val baseline = size.height * 0.75f // Y baseline for zero drinks
                    val points = drinksPerDay.mapIndexed { i, c ->
                        // Calculate y position based on count relative to max
                        Offset(i * spacing, baseline - if (max > 0) c / max.toFloat() * baseline * 0.7f else 0f)
                    }

                    // Draw straight lines connecting the points
                    points.windowed(2).forEach { drawLine(lineColor, it[0], it[1], 3f) }

                    // Draw filled area under the line
                    drawPath(androidx.compose.ui.graphics.Path().apply {
                        moveTo(points.first().x, baseline) // Start at first x at baseline
                        points.forEach { lineTo(it.x, it.y) } // Line to each point
                        lineTo(points.last().x, baseline) // Line down to baseline at last point
                        close() // Close the path
                    }, fillColor)

                    // Draw circles at each point
                    val r = 6.dp.toPx()
                    points.forEach { drawCircle(lineColor, r, it) }
                }
            }

            // Row showing numeric counts under each point
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                drinksPerDay.forEach {
                    Text("$it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondary)
                }
            }

            // Row showing day labels
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                days.forEach {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
