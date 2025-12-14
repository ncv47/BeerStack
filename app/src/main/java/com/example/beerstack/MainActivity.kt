package com.example.beerstack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.Image
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import com.example.beerstack.ui.theme.BeerStackTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import com.example.beerstack.viewmodel.BeerViewModel
import com.example.beerstack.model.Beer
import com.example.beerstack.model.Currency
import androidx.compose.material.icons.filled.AutoGraph
import com.example.beerstack.ui.theme.BeerGradient
//Utility Imports (Helper/Components Functions)
import com.example.beerstack.utils.sortBeers
import com.example.beerstack.utils.BeerList
import com.example.beerstack.components.SortDropdown
import com.example.beerstack.components.SearchBar
import com.example.beerstack.components.CurrencyToggle
import com.example.beerstack.components.SortOptions

//---MAIN SCREEN---

// Consult BaseActivity for root check
class MainActivity : BaseActivity() {

    // Called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() //for layout/UI
        // Get user ID passed from the previous activity (Login Screen)
        val userId = intent.getIntExtra("USER_ID", -1)
        // Get username
        val username = intent.getStringExtra("USER_NAME") ?: "Unknown"

        // Set the UI using Jetpack Compose
        setContent {
            // Apply won theme, not automatically from android
            BeerStackTheme(dynamicColor = false) {
                // Show the main screen and pass user data
                Main(
                    userId = userId,
                    username = username
                )
            }
        }
    }
}

@Composable
fun Main(userId: Int,username: String, beerViewModel: BeerViewModel = viewModel()){
    // BeerViewModel:... is a class that processes all the beer activities, here it is declared

    //For the Sort Function of the scrollable List
    var selectedSort by remember { mutableStateOf(SortOptions.NAME) }
    //For the search function
    var searchText by remember { mutableStateOf("") }
    //mutableStateOf: creates a state holder, if the value is changed, Compose will redraw the parts that use this
    // For example: the beer cards will ble updated when searched for something and API is refetched

    // Request the API when main() is loaded or search changes (on every key press)
    LaunchedEffect(searchText) {
        beerViewModel.getBeers(searchText)
    }

    //Refresh conversion Rate on load
    LaunchedEffect(Unit) { //Unit: no real meaning, run once at beginning then do nothing
        beerViewModel.refreshConversionRate()
    }

    // Make sure beers are sorted reactively when sort option changes
    val sortedBeers = sortBeers(
        beers = beerViewModel.beerList,
        sortOption = selectedSort

    )
    Box( //Used to stack elements on top of each other
        modifier = Modifier
            .fillMaxSize() //Over entire screen
            .background(BeerGradient)
    ) {
        // use Scaffold for top and bottom bars (Handles weight on its own, weight = how much space a composable takes relative to others)
        Scaffold( //Layout Structure, draws the top/bottom bar and body, but doesn't put them there yet
            containerColor = Color.Transparent,
            topBar = {
                //Values given trough with the TopBar
                TopBar(userId = userId, username = username)
            },
            bottomBar = {
                //Also values, including on which screen the user is currently on (true/false)
                BottomBar(userId = userId, username = username, currentScreenIsHome = true, currentScreenIsStack = false, currentScreenIsLeaderboard = false
                )
            }
        ) { innerPadding -> //automatically calculates padding
            Body(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                //Pass the viewmodel data down to the body composable
                beerViewModel = beerViewModel,
                beers = sortedBeers,
                error = beerViewModel.error,
                //For te sort functionalities
                selectedSort = selectedSort,
                onSortChange = { selectedSort = it },
                //For the search functionalities
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                //For Currency Conversion
                currency = beerViewModel.currency,
                eurPerUsd = beerViewModel.eurPerUsd,
                userId = userId,
                // NEW: loading flag from ViewModel
                isLoading = beerViewModel.isLoading
            )
        }
    }
}

//Now the different bars and boy are being actually composed = made

@Composable
fun TopBar(userId: Int, username: String, modifier: Modifier = Modifier){
    // Get the current Android context
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            //Increase overall top bar height
            .height(100.dp)
            .padding(
                top = 32.dp, //More Padding than the rest for the icons above
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        // Horizontal layout for logo and button
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App logo displayed on the left side
            Image(
                painter = painterResource(R.drawable.beerstacklogotransparent),
                contentDescription = "BeerStack Logo",
                modifier = Modifier
                    .height(48.dp)
            )

            // pushes the button to the far right
            Spacer(modifier = Modifier.weight(1f))

            // Login page button to the right
            FilledTonalButton(
                onClick = {
                    // Create intent to open FifthActivity (profile page)
                    val intent = Intent(context, FifthActivity::class.java)
                    intent.putExtra("USER_ID", userId)        // pass logged-in user ID
                    intent.putExtra("USER_NAME", username)    // pass logged-in username
                    context.startActivity(intent)                           // Start the activity

                },
                // Round pill-shaped button
                shape = RoundedCornerShape(50)
            ) {
                // Profile icon inside the button
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = stringResource(R.string.profile_page)
                )
                Spacer(modifier = Modifier.width(4.dp))             //SMall space between icon and text
                Text(stringResource(R.string.profile_page))     //Button text
            }
        }
    }
}

@Composable
fun Body(
    // Body displays  beer data or info messages
    modifier: Modifier = Modifier,
    beerViewModel: BeerViewModel,
    beers: List<Beer>,
    error: String?,
    //Sort
    selectedSort: SortOptions,
    onSortChange: (SortOptions) -> Unit,
    //Search
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    //Currency conversion
    currency: Currency,
    eurPerUsd: Double,
    userId: Int,
    // NEW: loading flag
    isLoading: Boolean
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth(),
        //.background(MaterialTheme.colorScheme.surfaceVariant), // Slightly darker background so cards pop more
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // TOP: Search on the left, sort currency converter & sort dropdown menu on the right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Large search box on the left
            SearchBar(
                value = searchText,
                onValueChange = onSearchTextChange,
                onSearch = { beerViewModel.getBeers(searchText) }, //When something is typed, get beers again (fetch API) with text
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(6.dp))

            //Button to convert dollar to euro and vise versa
            CurrencyToggle(
                currency = currency,
                onToggleAndRefresh = {
                    beerViewModel.toggleCurrency()
                    beerViewModel.refreshConversionRate()
                }
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Sort dropdown takes the place of the old Sort button
            SortDropdown(
                selectedSort = selectedSort,
                onSortChange = onSortChange
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))

        // Show loading, error, beer list, or "add beer" button
        when {
            isLoading -> {
                // Show loading while beers are being fetched
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> Text(text = error, color = Color.Red) // Show error if loading failed
            beers.isNotEmpty() -> BeerList(
                items = beers,
                onAddBeerClick = { beer ->
                    // Navigate to detail page for selected beer
                    val intent = Intent(context, FourthActivity::class.java)
                    intent.putExtra("beer_extra", beer) //must be parcelable object (@Parcelize)
                    intent.putExtra("USER_ID", userId)

                    context.startActivity(intent)
                },
                currency = currency,
                eurPerUsd = eurPerUsd
            )

            searchText.isNotBlank() -> {
                // Button to add your own beer
                Button(
                    onClick = {
                        // Go to screen with own beer
                        val intent = Intent(context, SeventhActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                ) {
                    Text("Beer not found, add it yourself!")
                }
            }
        }
    }
}

@Composable
fun BottomBar(userId: Int, username: String, currentScreenIsHome: Boolean, currentScreenIsStack: Boolean, currentScreenIsLeaderboard: Boolean, modifier: Modifier = Modifier){
    val context = LocalContext.current

    //keep state which item is selected
    var selectedItem by remember {
        mutableIntStateOf(
            when{
                currentScreenIsHome -> 0
                currentScreenIsStack -> 1
                currentScreenIsLeaderboard -> 2
                else -> 0
            }
        )
    }

    // Bottom navigation bar container
    NavigationBar(
        modifier = modifier
            .fillMaxWidth(),
        containerColor = Color.Transparent
        // background color comes from MaterialTheme by default
    ) {
        // -----Home button (MainActivity)-----
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = {
                selectedItem = 0
                val intent = Intent(context, MainActivity::class.java)
                if (!currentScreenIsHome) { // Navigate to MainActivity only if not already there
                    intent.putExtra("USER_ID", userId)
                    intent.putExtra("USER_NAME", username)
                    context.startActivity(intent)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = stringResource(R.string.home_page)
                )
            },
            label = { Text(stringResource(R.string.home_page)) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent  // no blue background when selected
            )
        )

        //-----Stack/Collection Button-----
        NavigationBarItem(
            selected = selectedItem == 1,  // Highlight if currently selected
            onClick = {
                selectedItem = 1
                if(!currentScreenIsStack) {
                    val intent = Intent(context, SecondActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    intent.putExtra("USER_NAME", username)
                    context.startActivity(intent)
                }
            },
            icon = {
                // Button as image/icon that you can click on
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,   // or Icons.Filled.List
                    contentDescription = stringResource(R.string.collection_page)
                )
            },
            label = { Text(stringResource(R.string.collection_page)) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent  // no blue background when selected
            )
        )

        // ----- Leaderboard Button -----
        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = {
                if (!currentScreenIsLeaderboard) {
                    selectedItem = 2
                    val intent = Intent(context, NinthActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    intent.putExtra("USER_NAME", username)
                    context.startActivity(intent)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.AutoGraph,
                    contentDescription = "Leaderboard"
                )
            },
            label = { Text("Leaderboard") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent  // no blue background when selected
            )
        )
    }
}

//Lets you see what main look like without running the app, for in android studio
@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun MainPreview() {
    // Apply the app's theme, not automatically
    BeerStackTheme(dynamicColor = false) {
        Main(userId = 3,username = "password", beerViewModel = viewModel())
    }
}
