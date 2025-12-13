package com.example.beerstack.components

import androidx.compose.foundation.background
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
//For Star Rating
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import com.example.beerstack.ui.theme.BeerStackTheme
import kotlin.math.floor
import kotlin.math.roundToInt
//Util Imports (Helper Functions)
import com.example.beerstack.utils.formatBeerPrice

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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StarRating(
                                    rating = beer.rating?.average   //
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = beer.rating?.average?.let { "%.1f/5".format(it) } ?: "N/A",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
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
                        Text("Add To Stack", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StarRating(
    rating: Double?,
    stars: Int = 5,
    modifier: Modifier = Modifier,
    starColor: Color = MaterialTheme.colorScheme.primary
) {
    val safeRating = (rating ?: 0.0).coerceIn(0.0, stars.toDouble())
    // Round to nearest 0.5
    val roundedToHalf = (safeRating * 2).roundToInt() / 2.0

    val fullStars = floor(roundedToHalf).toInt()
    val hasHalf = roundedToHalf % 1.0 >= 0.5
    val emptyStars = (stars - fullStars - if (hasHalf) 1 else 0).coerceAtLeast(0)

    Row(modifier = modifier) {
        // Full stars
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(18.dp)
            )
        }

        // Half star
        if (hasHalf) {
            Icon(
                painter = painterResource(id = R.drawable.half_star),
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(18.dp)
            )
        }

        // Empty stars
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}