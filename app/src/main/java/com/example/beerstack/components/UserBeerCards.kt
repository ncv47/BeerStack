package com.example.beerstack.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.beerstack.data.remote.UserBeerDto
import com.example.beerstack.R
import java.io.File
import android.os.Environment
import android.net.Uri
import androidx.compose.ui.platform.LocalContext




@Composable
fun UserBeerItemCard(
    beer: UserBeerDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth()
            .clickable() { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: image if present
            beer.imageurl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Beer photo",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 12.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.beerpicture_placeholder),
                    error = painterResource(R.drawable.beerpicture_placeholder)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right: name + your rating + average
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = beer.name,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "My Rating: %.1f".format(beer.myrating),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Average: %.1f".format(beer.apiaverage),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun UserBeerGroupCard(
    name: String,
    beersWithSameName: List<UserBeerDto>,
    onBeerClick: (UserBeerDto) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val representative = beersWithSameName.first()

    Card(
        modifier = modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            // HEADER: image | name + entries | expand icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                representative.imageurl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = "Beer image",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(end = 12.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.beerpicture_placeholder),
                        error = painterResource(R.drawable.beerpicture_placeholder)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Entries: ${beersWithSameName.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded)
                            Icons.Filled.ExpandLess
                        else
                            Icons.Filled.ExpandMore,
                        contentDescription = if (expanded) "Show less" else "Show more"
                    )
                }
            }

            // BODY: list of entries when expanded
            if (expanded) {
                Spacer(Modifier.height(8.dp))

                beersWithSameName.forEach { beer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBeerClick(beer) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {

                            val context = LocalContext.current
                            val headerModel = remember(beer.myphoto) {
                                beer.myphoto?.let { relative ->
                                    val picturesDir: File ?=
                                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                                    val fileName = relative.substringAfterLast('/')
                                    val file = File(picturesDir, fileName)
                                    if (file.exists()) Uri.fromFile(file) else null
                                }
                            }


                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = headerModel,
                                    contentDescription = "My beer photo",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .padding(end = 12.dp),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(R.drawable.beerpicture_placeholder),
                                    error = painterResource(R.drawable.beerpicture_placeholder)
                                )

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "My Rating: %.1f".format(beer.myrating),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Average: %.1f".format(beer.apiaverage),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Location: ${beer.location}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}