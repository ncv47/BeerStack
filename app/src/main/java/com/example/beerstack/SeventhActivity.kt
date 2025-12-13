package com.example.beerstack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.beerstack.data.remote.BeerDto
import com.example.beerstack.data.remote.SupabaseCollectionRepository
import com.example.beerstack.ui.theme.BeerStackTheme
import kotlinx.coroutines.launch

class SeventhActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val beerRepo = SupabaseCollectionRepository()

        setContent {
            BeerStackTheme {
                AddOwnBeerScreen(
                    onSave = { dto ->
                        lifecycleScope.launch {
                            try {
                                beerRepo.addBeerToBeerList(dto)
                                println("SUPABASE INSERT OK: $dto")
                                finish()
                            } catch (e: Exception) {
                                println("SUPABASE INSERT ERROR: ${e.message}")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AddOwnBeerScreen(
    onSave: (BeerDto) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var apiAverageText by remember { mutableStateOf("") }
    var totalReviewsText by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("USD") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Add your own beer",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Beer name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        // De currency button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = selectedCurrency == "USD",
                onClick = { selectedCurrency = "USD" },
                label = { Text("USD") }
            )
            FilterChip(
                selected = selectedCurrency == "EUR",
                onClick = { selectedCurrency = "EUR" },
                label = { Text("EUR") }
            )
        }

        //Format for the price
        fun validatePriceInput(input: String): String {
            val cleaned = input.replace(",", ".")
            val regex = Regex("^\\d{0,7}(\\.\\d{0,2})?$") // max 7 cijfers voor de punt, 2 erna (1 MILLION BEERS)
            return if (cleaned.isEmpty() || regex.matches(cleaned)) cleaned else priceText
        }

        OutlinedTextField(
            value = priceText,
            onValueChange = { new ->
                priceText = validatePriceInput(new)
            },
            label = { Text("Price (0.00)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = apiAverageText,
            onValueChange = { apiAverageText = it },
            label = { Text("API average") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = totalReviewsText,
            onValueChange = { totalReviewsText = it },
            label = { Text("Total reviews") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Stock image URL") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val price = priceText.ifBlank { "0.00" }
                val apiAvg = apiAverageText.toDoubleOrNull() ?: 0.0
                val totalReviews = totalReviewsText.toIntOrNull() ?: 0

                val dto = BeerDto(
                    id = null,            // API generates ID
                    name = name,
                    currency = selectedCurrency,
                    price = price,
                    apiaverage = apiAvg,
                    reviews = totalReviews,
                    imageurl = imageUrl
                )
                onSave(dto)
            },
            modifier = Modifier.fillMaxWidth(),
            // This field has to be filled in for the button to work
            enabled = name.isNotBlank()
        ) {
            Text("Save")
        }
    }
}

