package com.example.beerstack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
    Body(modifier = Modifier
        .fillMaxSize()
    )
}



@Composable
fun Body(modifier: Modifier = Modifier){
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(R.drawable.test),
            contentDescription = "BeerStack Logo"
        )
        Text(
            text="BeerStack",
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 24.dp)
        )

        BeerList(beerList)

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