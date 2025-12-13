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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.example.beerstack.ui.BeerViewModel
import com.example.beerstack.model.Beer
import com.example.beerstack.model.Currency
import com.example.beerstack.ui.theme.BeerGradient
//Util Imports (Helper Functions)
import com.example.beerstack.utils.sortBeers
import com.example.beerstack.utils.BeerList
import com.example.beerstack.utils.SortDropdown
import com.example.beerstack.utils.SearchBar
import com.example.beerstack.utils.CurrencyToggle
import com.example.beerstack.utils.SortOptions

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        val userId = intent.getIntExtra("USER_ID", -1)
        val username = intent.getStringExtra("USER_NAME") ?: "Unknown"
        setContent {
            BeerStackTheme(dynamicColor = false) {
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


    //For the Sort Function of the scrollable List
    var selectedSort by remember { mutableStateOf(SortOptions.NAME) }
    //For the search function
    var searchText by remember { mutableStateOf("") }

    // Request the API when main() is loaded or search changes
    LaunchedEffect(searchText) {
        beerViewModel.getBeers(searchText)
    }

    //Refresh conversion Rate on load
    LaunchedEffect(Unit) {
        beerViewModel.refreshRate()
    }

    // Make sure beers are sorted reactively when sort option changes
    val sortedBeers by remember(selectedSort, beerViewModel.beerList) {
        mutableStateOf(sortBeers(beerViewModel.beerList, selectedSort))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BeerGradient)
    ) {

        // use Scaffold for top and bottom bars (Handles weight on its own)
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopBar(userId = userId, username = username)
            },
            bottomBar = {
                BottomBar(userId = userId, username = username, currentScreenIsHome = true, currentScreenIsStack = false
                )
            }
        ) { innerPadding ->
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
                userId = userId
            )
        }
    }
}

@Composable
fun TopBar(userId: Int, username: String, modifier: Modifier = Modifier){
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
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App logo on the left, bigger and centered vertically
            Image(
                painter = painterResource(R.drawable.beerstacklogotransparent),
                contentDescription = "BeerStack Logo",
                modifier = Modifier
                    .height(48.dp),
                contentScale = ContentScale.Fit
            )

            // Take up remaining space between logo and button
            Spacer(modifier = Modifier.weight(1f))

            // Login page button to the right
            FilledTonalButton(
                onClick = {
                    val intent = Intent(context, FifthActivity::class.java)
                    intent.putExtra("USER_ID", userId)        // pass logged-in user ID
                    intent.putExtra("USER_NAME", username)    // pass logged-in username
                    context.startActivity(intent)

                },
                // Round pill-shaped button
                shape = RoundedCornerShape(50)
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = stringResource(R.string.profile_page)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.profile_page))
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
    //Currency COnversion
    currency: Currency,
    eurPerUsd: Double,
    userId: Int
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth(),
            //.background(MaterialTheme.colorScheme.surfaceVariant), // Slightly darker background so cards pop more
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // TOP: Search on the left, sort currency converter & sort dropdownmenu on the right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp) ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Large search box on the left
            SearchBar(
                value = searchText,
                onValueChange = onSearchTextChange,
                onSearch = { beerViewModel.getBeers(searchText) },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(6.dp))

            //Button to convert dollar to euro and vise versa
            CurrencyToggle(
                currency = currency,
                onToggleAndRefresh = {
                    beerViewModel.toggleCurrency()
                    beerViewModel.refreshRate()
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

        when {
            error != null -> Text(text = error, color = Color.Red) // Show error if loading failed
            beers.isNotEmpty() -> BeerList(
                items = beers,
                onAddBeerClick = { beer ->
                    val intent = Intent(context, FourthActivity::class.java)
                    intent.putExtra("beer_extra", beer) //must be parcelable
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

        //Get last successful fetched beer & last error message
        val lastAddedBeerName = beerViewModel.lastAddedBeerName
        val lastAddedBeerError = beerViewModel.lastAddedBeerError

        if (lastAddedBeerName != null || lastAddedBeerError != null) {
            Dialog(onDismissRequest = { beerViewModel.clearLastBeerInfo() }) { //Dialog = pop up
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(24.dp)
                        .background(Color.White, shape = RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            //Show message on message depending on success or error
                            text = lastAddedBeerName?.let {
                                "Added '$it' to Stack!"
                            } ?: lastAddedBeerError.orEmpty(),
                            color = if (lastAddedBeerError != null) Color.Red else Color.Black,
                            modifier = Modifier.padding(20.dp)
                        )
                        Button(
                            onClick = { beerViewModel.clearLastBeerInfo() },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar(userId: Int, username: String, currentScreenIsHome: Boolean, currentScreenIsStack: Boolean, modifier: Modifier = Modifier){
    val context = LocalContext.current

    //keep state which item is selected
    var selectedItem by remember {
        mutableIntStateOf(
        when{
            currentScreenIsHome -> 0
            currentScreenIsStack -> 1
            else -> 0
            }
        )
    }

    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = Color.Transparent
        // background color comes from MaterialTheme by default
    ) {
        // Home button (MainActivity)
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = {
                selectedItem = 0
                val intent = Intent(context, MainActivity::class.java)
                if (!currentScreenIsHome) {
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

        //Button to the second page with the database of beer collection
        NavigationBarItem(
            selected = selectedItem == 1,
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
    }
}


@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun MainPreview() {
    BeerStackTheme(dynamicColor = false) {
        Main(userId = 3,username = "password", beerViewModel = viewModel())
    }
}