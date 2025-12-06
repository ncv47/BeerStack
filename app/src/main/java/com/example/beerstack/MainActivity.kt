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
import androidx.compose.ui.unit.sp
import com.example.beerstack.ui.theme.BeerStackTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
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

    // use Scaffold for top and bottom bars
    Scaffold(
        topBar = {
            Header() //no weight anymore, Scaffold handles sizing
        },
        bottomBar = {
            Footer() //no weight here either
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
fun Header(modifier: Modifier = Modifier){
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Yellow)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Add space to the left
        Spacer(modifier = Modifier.weight(1f))

        // Centered image and text, over the whole width of the page
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            //2f = twice as big
            modifier = Modifier.weight(2f)
        ) {
            Image(
                painter = painterResource(R.drawable.beerstacklogo),
                contentDescription = "BeerStack Logo"
            )
            Text(
                text = "BeerStack",
                fontSize = 24.sp
            )
        }

        // Login page button to the right
        Button(
            onClick = {
                val intent = Intent(context, ThirdActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.login_page))
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
            .fillMaxWidth()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        SearchBar(
            value = searchText,
            onValueChange = onSearchTextChange,
            onSearch = { beerViewModel.getBeers(searchText) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        SortDropdown(selectedSort = selectedSort, onSortChange = onSortChange)
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
fun Footer(modifier: Modifier = Modifier){
    val context = LocalContext.current

    Column (
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Green)
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Button to add a own, new beer that ist in the API
        Button(onClick = { /*TODO*/ }) {
            Text(stringResource(R.string.add_beer))
        }

        //Button to the second page with the database of beer collection
        Button(onClick = {
            val intent = Intent(context, SecondActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(stringResource(R.string.collection_page))
        }
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