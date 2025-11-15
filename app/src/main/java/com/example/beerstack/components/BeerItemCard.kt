package com.example.beerstack.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import com.example.beerstack.model.Beer

@Composable
fun BeerItemCard(beer: Beer) {
    Card(
        //Spacing
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        //SHadown for the boxes and round corners
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // If there is an image given with the API (not always the case), show it
            beer.image?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Beer photo",
                    modifier = Modifier.size(70.dp).padding(end = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
            //Show beer details to the right of the image
            Column(modifier = Modifier.weight(1f)) {
                Text(beer.name, fontSize = 20.sp, color = Color.Black)
                //show price if there is one given
                beer.price?.let { Text("Price: $it", fontSize = 14.sp, color = Color.DarkGray) }
                //Always show the ID
                Text("ID: ${beer.id}", fontSize = 10.sp, color = Color.LightGray)
                //For the JSON (2 seperate values)
                beer.rating?.let {
                    Text("Rating: %.2f".format(it.average), fontSize = 14.sp, color = Color.DarkGray)
                    Text("Reviews: ${it.reviews}", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}