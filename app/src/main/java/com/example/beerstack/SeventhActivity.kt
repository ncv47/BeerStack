package com.example.beerstack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.beerstack.data.remote.SupabaseBeerDto
import com.example.beerstack.data.remote.SupabaseCollectionRepository
import com.example.beerstack.ui.theme.BeerGradient
import com.example.beerstack.ui.theme.BeerStackTheme
import kotlinx.coroutines.launch

class SeventhActivity : BaseActivity() {

//---ADD OWN BEER SCREEN---

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val beerRepo = SupabaseCollectionRepository()

        setContent {
            BeerStackTheme () {
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
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
fun AddOwnBeerScreen(
    onSave: (SupabaseBeerDto) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("USD") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BeerGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                GeneralTopBar(onBack = onBack)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
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

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Beer name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = appTextFieldColors()
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
                    val regex =
                        Regex("^\\d{0,7}(\\.\\d{0,2})?$") // max 7 cijfers voor de punt, 2 erna (1 MILLION BEERS)
                    return if (cleaned.isEmpty() || regex.matches(cleaned)) cleaned else priceText
                }

                TextField(
                    value = priceText,
                    onValueChange = { new ->
                        priceText = validatePriceInput(new)
                    },
                    label = { Text("Price (0.00)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = appTextFieldColors()
                )

                TextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Stock image URL") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = appTextFieldColors()
                )

                Spacer(modifier = Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = {
                        val price = priceText.ifBlank { "0.00" }

                        val dto = SupabaseBeerDto(
                            id = null,            // API generates ID
                            name = name,
                            currency = selectedCurrency,
                            price = price,
                            apiaverage = 0.0,
                            reviews = 0,
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
    }
}

//Made this so you can reuse these same colors everywhere without having to copy paste
@Composable
fun appTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
    unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
)
