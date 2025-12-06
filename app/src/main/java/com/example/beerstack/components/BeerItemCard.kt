package com.example.beerstack.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import com.example.beerstack.model.Beer
import com.example.beerstack.model.Currency
import androidx.compose.ui.res.painterResource
import com.example.beerstack.R
import com.example.beerstack.MainActivity.* //For formatbeerprice function, later put in helper function



@Composable
fun BeerItemCard(
    beer: Beer,
    onAddToCollection: () -> Unit,
    currency: Currency,
    eurPerUsd: Double,
    modifier: Modifier = Modifier
) {
    Card(
        //Spacing
        modifier = modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth(),
        //Shadow for the boxes and more round corners
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: ID above image in the same column, stuck to top-left
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "ID: ${beer.id}",
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                )

                // If there is an image given with the API (not always the case), show it
                beer.image?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = "Beer photo",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(end = 12.dp),
                        contentScale = ContentScale.Crop,
                        //Placeholder if there is an error for the picture
                        placeholder = painterResource(R.drawable.beerpicture_placeholder),
                        error = painterResource(R.drawable.beerpicture_placeholder)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right side: text + button
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Top row: title at same vertical level as ID
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = beer.name,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }

                // Stats column + button row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Price, rating, reviews under each other
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        beer.price?.let {
                            Text(
                                text = "Price: " + formatBeerPrice(
                                    rawPrice = it,
                                    currency = currency,
                                    eurPerUsd = eurPerUsd
                                ),
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                        }
                        beer.rating?.let {
                            Text(
                                text = "Rating: %.2f".format(it.average),
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Reviews: ${it.reviews}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Button to the right of the stats
                    FilledTonalButton(
                        onClick = onAddToCollection,
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Add To Collection", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}