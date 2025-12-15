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
import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.beerstack.ui.theme.BeerGradient

//---ADD TO STACK (BEER TO COLLECTION)----

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
            BeerStackTheme (dynamicColor = false) {
                if (beer != null && userId != -1) {
                    RateBeerScreen(
                        beer = beer,
                        onBack = { finish() },
                        onDone = { rating, location, notes, myPhotoPath ->
                            lifecycleScope.launch {
                                // API-average uit Beer; als null, gebruik je eigen rating
                                try {
                                    val apiAvg = beer.rating?.average ?: rating.toDouble()

                                    val dto = UserBeerDto(
                                        userid = userId,
                                        beerid = beer.id,
                                        name = beer.name,
                                        currency = beer.currency,
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
    onDone: (Float, String, String, String?) -> Unit,   // laatste param = mijn foto (pad of URL)
    onBack: () -> Unit
) {
    //var rating by remember { mutableFloatStateOf(beer.rating?.average?.toFloat() ?: 0f) }
    var myRating by remember { mutableFloatStateOf(2.5f) }
    var notes by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var myPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingUri != null) {
            myPhotoUri = pendingUri    // now file definitely exists
        }
    }

    // Start the thing for asking camera permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
        // If granted it will launch the camera
    ) { granted ->
        val uri = pendingUri
        if (granted && uri != null) {
            cameraLauncher.launch(uri)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BeerGradient)
    ){
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                AddingTopBar(onBack = onBack)
            }
        ){ innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
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
                    text = "Rating: ${"%.1f".format(myRating)} / 5",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Start)
                )

                StarRatingBar(
                    rating = myRating,
                    onRatingChange = { myRating = it }
                )

                /* To be replaced with actual stars
                Slider(
                    value = rating,
                    onValueChange = { rating = it },
                    valueRange = 0f..5f,
                    steps = 9    // 0, 0,5 1 1,5...
                )
                */

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

                FilledTonalButton(
                    onClick = {
                        // App specific Pictures directory
                        val dir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
                        val file = File.createTempFile("mybeer_${beer.id}_", ".jpg", dir)
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

                        pendingUri = uri

                        //First ask camera permission
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Text("Take picture")
                }

                Spacer(modifier = Modifier.height(8.dp))

                FilledTonalButton(
                    onClick = {
                        onDone(myRating, location, notes, myPhotoUri?.path)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun StarRatingBar(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    maxRating: Int = 5
) {
    Row {
        for (i in 1..maxRating) {
            val starValue = i.toFloat()

            // choose icon for display
            val icon = when {
                rating >= starValue       -> Icons.Filled.Star        // full
                rating >= starValue - 0.5f -> Icons.AutoMirrored.Filled.StarHalf   // half
                else                       -> Icons.Outlined.StarBorder    // empty
            }

            StarIcon(
                starValue = starValue,
                icon = icon,
                onRatingChange = onRatingChange
            )
        }
    }
}

@Composable
private fun StarIcon(
    starValue: Float,
    icon: ImageVector,
    onRatingChange: (Float) -> Unit,
    starColor: Color = MaterialTheme.colorScheme.background
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = starColor,
        modifier = Modifier
            .size(32.dp)
            .combinedClickable(
                onClick = {
                    // long press → half star (0.5,1.5,2.5,...)
                    onRatingChange(starValue - 0.5f)
                },
                onDoubleClick = {
                    // single tap → full star (1,2,3,...)
                    onRatingChange(starValue)
                }
            )
    )
}


@Composable
fun AddingTopBar(onBack: () -> Unit) {
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