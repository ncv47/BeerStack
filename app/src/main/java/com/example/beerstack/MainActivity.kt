package com.example.beerstack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.Image
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeerStackTheme {
                BeerStack()
            }
        }
    }
}

@Preview
@Composable
fun BeerStack(){
    Column(modifier = Modifier.fillMaxSize()) {
        Header(modifier = Modifier.weight(0.15f))
        Body(modifier = Modifier.weight(0.7f))
        Footer(modifier = Modifier.weight(0.15f))
    }
}


@Composable
fun Header(modifier: Modifier = Modifier){
    Column (
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Yellow)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.test),
            contentDescription = "BeerStack Logo"
        )
        Text(
            text="BeerStack",
            fontSize = 24.sp,
        )
    }
}

@Composable
fun Body(modifier: Modifier = Modifier){
    Column (
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        BeerList(beerList)

    }

}

@Composable
fun Footer(modifier: Modifier = Modifier){
    Column (
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Green)
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = { /*TODO*/ }) {
            Text(stringResource(R.string.add_beer))
        }

        Button(onClick = { /*TODO*/ }) {
            Text(stringResource(R.string.collection_page))
        }
    }

}

val beerList = listOf(
    com.example.beerstack.model.BeerItem(1, "Test Beer"),
    com.example.beerstack.model.BeerItem(2, "Another Beer")
)

@Composable
fun BeerList(items: List<com.example.beerstack.model.BeerItem>) {
    LazyColumn {
        items(items) { beer ->
            Text(
                text = beer.beerName,
                modifier = Modifier.padding(12.dp),
                fontSize = 20.sp
            )
        }
    }
}