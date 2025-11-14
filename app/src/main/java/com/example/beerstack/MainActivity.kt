package com.example.beerstack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.Image
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beerstack.ui.BeerViewModel
import com.example.beerstack.model.Beer

class MainActivity : ComponentActivity() {
    //OVerite onCreate, when the activity is start/page is launced
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
    //Header body and footer sections underneath each other
    Column(modifier = Modifier.fillMaxSize()) {
        //Each Section with its height size
        Header(modifier = Modifier.weight(0.15f))
        Body(
            modifier = Modifier.weight(0.7f),
            beerViewModel = beerViewModel,
            beers = beerViewModel.beerList,
            error = beerViewModel.error
        )
        Footer(modifier = Modifier.weight(0.15f))
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
                painter = painterResource(R.drawable.test),
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
    modifier: Modifier = Modifier,
    beerViewModel: BeerViewModel,
    beers: List<Beer>,
    error: String?
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { beerViewModel.getBeers() }) {
            Text("Test API Connection")
        }
        when {
            error != null -> Text(text = error, color = Color.Red)
            beers.isNotEmpty() -> BeerList(beers)
            else -> Text(text = "No data loaded yet.")
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

        //BUtton to the second page with the database of beer collection
        Button(onClick = {
            val intent = Intent(context, SecondActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(stringResource(R.string.collection_page))
        }
    }

}

//Test Example data for the srollable list
val beerList = listOf(
    com.example.beerstack.model.BeerItem(1, "Test Beer"),
    com.example.beerstack.model.BeerItem(2, "Another Beer")
)

//Show from the API the beers (now temporary the test data) in a scrolling list, using LazyColumn
@Composable
fun BeerList(items: List<Beer>) {
    LazyColumn {
        items(items) { beer ->
            Text(
                text = beer.name,
                modifier = Modifier.padding(12.dp),
                fontSize = 20.sp
            )
        }
    }
}