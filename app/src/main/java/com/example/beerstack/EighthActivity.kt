package com.example.beerstack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.beerstack.data.remote.UserBeerDto
import com.example.beerstack.ui.theme.BeerGradient
import com.example.beerstack.ui.theme.BeerStackTheme

//---INDIVIDUAL BEERS IN STACK---

class EighthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Suppresses deprecation warning if API > 33 and makes it backwards compatible
        val beer: UserBeerDto? = if (android.os.Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("beer_entry", UserBeerDto::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("beer_entry")
        }

        setContent {
            BeerStackTheme(dynamicColor = false) {
                if (beer != null) {
                    BeerDetailScreen(
                        beer = beer,
                        onBack = { finish() }
                    )
                } else {
                    Text("No beer data")
                }
            }
        }
    }
}

@Composable
fun BeerDetailScreen(
    beer: UserBeerDto,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BeerGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                DetailTopBar(onBack = onBack)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Top: self-made picture, Untappd-style header
                val headerModel = remember(beer.myphoto) {
                    beer.myphoto?.let { relative ->
                        val picturesDir =
                            context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
                        val fileName = relative.substringAfterLast('/')
                        val file = java.io.File(picturesDir, fileName)
                        if (file.exists()) android.net.Uri.fromFile(file) else null
                    }
                }

                AsyncImage(
                    model = headerModel,
                    contentDescription = "My beer photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.beerpicture_placeholder),
                    error = painterResource(R.drawable.beerpicture_placeholder)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stock image on the left
                    beer.imageurl?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Beer image",
                            modifier = Modifier
                                .size(72.dp)
                                .padding(end = 12.dp),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.beerpicture_placeholder),
                            error = painterResource(R.drawable.beerpicture_placeholder)
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Name: ${beer.name}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row {
                            Text(
                                text = "My Rating:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            StarRating(rating = beer.myrating.toFloat())
                            Text(beer.myrating.toString())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Notes and location
                Text(
                    text = "Location: ${beer.location.orEmpty()}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Notes: ${beer.notes.orEmpty()}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun StarRating(
    rating: Float,
    maxRating: Int = 5,
    starColor: Color = MaterialTheme.colorScheme.primary
) {
    Row {
        for (i in 1..maxRating) {
            val starValue = i.toFloat()
            val icon = when {
                rating >= starValue -> Icons.Filled.Star
                rating >= starValue - 0.5f -> Icons.AutoMirrored.Filled.StarHalf

                else -> Icons.Outlined.StarBorder
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = starColor
            )
        }
    }
}


@Composable
fun DetailTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row ( // This extra row is so that the arrow and the return text acts like on button
            modifier = Modifier
                .clip(RoundedCornerShape(50)) // Makes it so that when you click it the shadow doesn't look like one big block but is an actual nice rounded shape that just fits
                .clickable{ onBack() } // Makes it so that the arrow and the text "return" are clickable to go back
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Return",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}