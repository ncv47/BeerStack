package com.example.beerstack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.beerstack.model.Beer
import com.example.beerstack.ui.theme.BeerStackTheme
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.beerstack.data.BeerDB.AppDataContainer
import com.example.beerstack.data.BeerDB.Item
import com.example.beerstack.data.remote.SupabaseCollectionRepository
import com.example.beerstack.data.remote.UserBeerDto

class FourthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val beer = intent.getParcelableExtra<Beer>("beer_extra")
        val userId = intent.getIntExtra("USER_ID", -1)

        val repository = AppDataContainer(this).itemsRepository

        val supabaseRepo = SupabaseCollectionRepository()

        setContent {
            BeerStackTheme {
                if (beer != null && userId != -1) {
                    RateBeerScreen(
                        beer = beer,
                        onDone = { rating, location, notes ->
                            lifecycleScope.launch {
                                // API-average uit Beer; als null, gebruik je eigen rating
                                try {
                                    val apiAvg = beer.rating?.average ?: rating.toDouble()

                                    val dto = UserBeerDto(
                                        userid = userId,
                                        beerid = beer.id,
                                        name = beer.name,
                                        price = beer.price,
                                        myrating = rating.toDouble(),
                                        apiaverage = apiAvg,
                                        imageurl = beer.image
                                    )

                                    supabaseRepo.addBeerToCollection(dto)

                                    // tijdelijke debug
                                    println("SUPABASE INSERT OK: $dto")
                                    finish()
                                } catch (e: Exception) {
                                    println("SUPABASE INSERT ERROR: ${e.message}")
                                }
                            }
                        }
                    )
                } else {
                    Text("No beer data received or user missing")
                }
            }
        }
    }

}


@Composable
fun RateBeerScreen(
    beer: Beer,
    onDone: (Float, String, String) -> Unit
) {
    var rating by remember { mutableFloatStateOf(beer.rating?.average?.toFloat() ?: 0f) }
    var notes by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("")}

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            beer.image?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Beer image",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(end = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Text(text = "Beer: ${beer.name}")
        }
        Spacer(Modifier.height(8.dp))

        Slider(
            value = rating,
            onValueChange = { rating = it },
            valueRange = 0f..5f,
            steps = 19    // 0, 0.25, 0.5, ... , 5.0
        )

        OutlinedTextField(
            value = location,
            onValueChange = {location = it},
            label = { Text("Location")}
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") }
        )

        Button(onClick = { onDone(rating, location, notes) }) {
            Text("Save")
        }
    }
}
