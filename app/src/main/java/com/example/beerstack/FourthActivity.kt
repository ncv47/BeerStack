package com.example.beerstack

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.lifecycleScope
import com.example.beerstack.model.Beer
import com.example.beerstack.ui.theme.BeerStackTheme
import com.example.beerstack.data.remote.SupabaseCollectionRepository
import com.example.beerstack.data.remote.UserBeerDto
import kotlinx.coroutines.launch
import java.io.File
import androidx.core.content.FileProvider

class FourthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Suppresses deprecation warning if API > 33 and makes it backwards compatible
        val beer: Beer? = if (android.os.Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("beer_extra", Beer::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("beer_extra")
        }

        val userId = intent.getIntExtra("USER_ID", -1)

        val supabaseRepo = SupabaseCollectionRepository()

        setContent {
            BeerStackTheme {
                if (beer != null && userId != -1) {
                    RateBeerScreen(
                        beer = beer,
                        onDone = { rating, location, notes, myPhotoPath ->
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
                                        notes = notes,
                                        location = location,
                                        imageurl = beer.image,
                                        myphoto = myPhotoPath
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
    onDone: (Float, String, String, String?) -> Unit   // laatste param = mijn foto (pad of URL)
) {
    var rating by remember { mutableFloatStateOf(beer.rating?.average?.toFloat() ?: 0f) }
    var notes by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var myPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
            myPhotoUri = if (success) pendingUri else null    // now file definitely exists
            pendingUri = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            beer.image?.let { url ->
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
            Text(
                text = "Beer: ${beer.name}",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Rating: ${"%.1f".format(rating)} / 5",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Slider(
            value = rating,
            onValueChange = { rating = it },
            valueRange = 0f..5f,
            steps = 9    // 0, 0,5 1 1,5...
        )

        // Preview of the picture
        myPhotoUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "My photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(vertical = 16.dp),
                contentScale = ContentScale.FillWidth,
            )
        }

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                // App specific Pictures directory
                val dir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
                val file = File.createTempFile("mybeer_${beer.id}_", ".jpg", dir)
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

                pendingUri = uri
                cameraLauncher.launch(uri)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Text("Take picture")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                onDone(rating, location, notes, myPhotoUri?.path)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
