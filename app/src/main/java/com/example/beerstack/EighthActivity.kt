package com.example.beerstack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.beerstack.data.remote.UserBeerDto
import com.example.beerstack.ui.theme.BeerStackTheme

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
            BeerStackTheme {
                if (beer != null) {
                    BeerDetailScreen(beer = beer)
                } else {
                    Text("No beer data")
                }
            }
        }
    }
}

@Composable
fun BeerDetailScreen(beer: UserBeerDto) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Top: self-made picture, Untappd-style header
        val headerModel = remember(beer.myphoto) {
            beer.myphoto?.let { relative ->
                val picturesDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
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
                Text(
                    text = "My Rating: %.1f".format(beer.myrating),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Notes and location
        Text(
            text = "Location: ${beer.location.orEmpty()}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Notes: ${beer.notes.orEmpty()}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
