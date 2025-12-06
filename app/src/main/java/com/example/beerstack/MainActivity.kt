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
import androidx.compose.ui.tooling.preview.Preview
import com.example.beerstack.ui.theme.BeerStackTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.List
import com.example.beerstack.ui.BeerViewModel
import com.example.beerstack.model.Beer
import com.example.beerstack.components.BeerItemCard

class MainActivity : BaseActivity() {
    //OVerite onCreate, when the activity is start/page is launched
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //UI THeme
            BeerStackTheme {
                //Call the main function
                Main()
            }
        }
    }
}

@Preview
@Composable
fun Main(beerViewModel: BeerViewModel = viewModel()){
    //For the Sort Function of the scrollable List
    var selectedSort by remember { mutableStateOf(SortOption.NAME) }
    //For the search function
    var searchText by remember { mutableStateOf("") }

    // Request the API when main() is loaded or search changes
    LaunchedEffect(searchText) {
        beerViewModel.getBeers(searchText)
    }

    // Make sure beers are sorted reactively when sort option changes
    val sortedBeers by remember(selectedSort, beerViewModel.beerList) {
        mutableStateOf(sortBeers(beerViewModel.beerList, selectedSort))
    }

    // use Scaffold for top and bottom bars (Handles weight on its own)
    Scaffold(
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomBar()
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
            onSearchTextChange = { searchText = it }
        )
    }
}

@Composable
fun TopBar(modifier: Modifier = Modifier){
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            //Use theme color instead of hardcoded yellow background
            .background(MaterialTheme.colorScheme.primary)
            //Increase overall top bar height
            .height(72.dp)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App logo on the left, bigger and centered vertically
            Image(
                painter = painterResource(R.drawable.beerstacklogo),
                contentDescription = "BeerStack Logo",
                modifier = Modifier
                    .height(48.dp)
            )

            // Take up remaining space between logo and button
            Spacer(modifier = Modifier.weight(1f))

            // Login page button to the right
            FilledTonalButton(
                onClick = {
                    val intent = Intent(context, ThirdActivity::class.java)
                    context.startActivity(intent)
                },
                // Round pill-shaped button
                shape = RoundedCornerShape(50)
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = stringResource(R.string.login_page)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.login_page))
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
    selectedSort: SortOption,
    onSortChange: (SortOption) -> Unit,
    //Search
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Search on the left, sort button on the right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Large search box
            SearchBar(
                value = searchText,
                onValueChange = onSearchTextChange,
                onSearch = { beerViewModel.getBeers(searchText) },
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Sort button (same visual weight as login)
            FilledTonalButton(
                onClick = { /* open dropdown via state below */ },
                shape = RoundedCornerShape(50),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Sort")
            }
        }

        // Actual dropdown for sort options
        SortDropdown(
            selectedSort = selectedSort,
            onSortChange = onSortChange
        )

        Spacer(modifier = Modifier.padding(8.dp))
        when {
            error != null -> Text(text = error, color = Color.Red) // Show error if loading failed
            beers.isNotEmpty() -> BeerList(
                items = beers,
                onAddBeerClick = { beer ->
                    val intent = Intent(context, FourthActivity::class.java)
                    intent.putExtra("beer_extra", beer) //must be parcelable

                    context.startActivity(intent)
                }
            )

            searchText.isNotBlank() -> Text("No beers found")
            else -> Text(text = "Loading...")
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
                                "Added '$it' to collection!"
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
fun BottomBar(modifier: Modifier = Modifier){
    val context = LocalContext.current

    //keep state which item is selected
    var selectedItem by remember { mutableIntStateOf(0) }

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
        // background color comes from MaterialTheme by default
    ) {
        //Button to add a own, new beer that ist in the API
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = {
                selectedItem = 0
                //TODO: navigate to "add own beer" screen
            },
            icon = {
                // Button as image/icon that you can click on
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_beer)
                )
            },
            label = { Text(stringResource(R.string.add_beer)) }
        )

        //Button to the second page with the database of beer collection
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = {
                selectedItem = 1
                val intent = Intent(context, SecondActivity::class.java)
                context.startActivity(intent)
            },
            icon = {
                // Button as image/icon that you can click on
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,   // or Icons.Filled.List
                    contentDescription = stringResource(R.string.collection_page)
                )
            },
            label = { Text(stringResource(R.string.collection_page)) }
        )
    }
}

//Show from the API the beers (now temporary the test data) in a scrolling list, using LazyColumn
// beer = for scrollable list
// OnGetBeerByID = for the collection specific fetch
@Composable
fun BeerList(
    items: List<Beer>,
    onAddBeerClick: (Beer) -> Unit
) {
    LazyColumn {
        items(items) { beer ->
            BeerItemCard(
                beer = beer,
                onAddToCollection = { onAddBeerClick(beer) }
            )
        }
    }
}


//Options for the sort function
enum class SortOption(val label: String) {
    NAME("A-Z"),
    NAME_REVERSE("Z-A"),
    PRICE("Price ↑"),
    PRICE_REVERSE("Price ↓"),
    RATING("Rating ↑"),
    RATING_REVERSE("Rating ↓")
}

//The sort functionality in a dropdown menu
@Composable
fun SortDropdown(
    selectedSort: SortOption,
    onSortChange: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) {
            Text("Sort: ${selectedSort.label}")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSortChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

//The sort dropdown functionality
fun sortBeers(beers: List<Beer>, sortOption: SortOption): List<Beer> {
    return when (sortOption) {
        SortOption.NAME -> beers.sortedBy { it.name }
        SortOption.NAME_REVERSE -> beers.sortedByDescending { it.name }
        SortOption.PRICE -> beers.sortedBy { it.price?.replace("$", "")?.toDoubleOrNull() ?: Double.MAX_VALUE }
        SortOption.PRICE_REVERSE -> beers.sortedByDescending { it.price?.replace("$", "")?.toDoubleOrNull() ?: Double.MIN_VALUE }
        SortOption.RATING -> beers.sortedBy { it.rating?.average ?: Double.MIN_VALUE }
        SortOption.RATING_REVERSE -> beers.sortedByDescending { it.rating?.average ?: Double.MAX_VALUE }
    }
}

//Searchbar functionality
@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Search beers...") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() })
    )
}