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
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
            BeerStackTheme () {
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
                GeneralTopBar (onBack = onBack)
            }
        ){ innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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

                TextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = AppTextFieldColors()
                )

                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp)
                        .padding(top = 8.dp),
                    colors = AppTextFieldColors()
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

//This is the 'starrating slider' its not actually a slider
@Composable
fun StarRatingBar(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    maxRating: Int = 5
) {
    Row {
        for (i in 1..maxRating) { //makes 5 stars
            val starValue = i.toFloat() // keeps track of on which star we are working on now from 1 to 5 first 1

            // choose icon for display
            val icon = when {
                rating >= starValue       -> Icons.Filled.Star        // gives a full star if the rating is bigger or eq to the star we are on now
                rating >= starValue - 0.5f -> Icons.AutoMirrored.Filled.StarHalf   // Gives a half star if the rating is bigger or eq to the star we are on now - .5
                else                       -> Icons.Outlined.StarBorder    // gives an empty star in all other cases
            }
            //For example rating is 3.5 it goes to the first star 1 the rating 3.5 > 1 so give a full star second star 3.5 > 2 so full star third star 3.5 > 3 so full star 3.5 !> 4 so next question is 3.5 >= 3.5 yes so half star next and last 3.5 > 5 no next 3.5 >= 4.5 no so empty star

            StarIcon( // gives the actual stars
                starValue = starValue,
                icon = icon,
                onRatingChange = onRatingChange
            )
        }
    }
}

//Gives the actual stars you can see
@Composable
private fun StarIcon(
    starValue: Float,
    icon: ImageVector,
    onRatingChange: (Float) -> Unit,
    starColor: Color = MaterialTheme.colorScheme.background
) {
    Icon( //uses an icon to create the stars
        imageVector = icon, // the image is the icon given from above so half full or empty
        contentDescription = null,
        tint = starColor, // gives the correct color to the stars so in this case the theme background is not used anywhere else only for stars
        modifier = Modifier
            .size(32.dp)
            .combinedClickable( // makes it so if you click once you get a half star
                onClick = {
                    // long press → half star (0.5,1.5,2.5,...)
                    onRatingChange(starValue - 0.5f)
                },
                onDoubleClick = { // makes it so if you double click you get a full star
                    // single tap → full star (1,2,3,...)
                    onRatingChange(starValue)
                }
            )
    )
}