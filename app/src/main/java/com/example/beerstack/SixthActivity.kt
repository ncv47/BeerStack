package com.example.beerstack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.beerstack.data.UserDB.User
import com.example.beerstack.data.AppDataContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.beerstack.data.UserDB.OfflineUsersRepository
import com.example.beerstack.ui.theme.BeerGradient
import com.example.beerstack.ui.theme.BeerStackTheme
import io.ktor.util.encodeBase64

//---REGISTER SCREEN---

class SixthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AppDataContainer(this).usersRepository

        setContent {
            BeerStackTheme(dynamicColor = false) {
                RegisterScreen(
                    onRegister = { username, password ->
                        lifecycleScope.launch {
                            // Check if username is already taken
                            val exists = withContext(Dispatchers.IO) {
                                if (repository is OfflineUsersRepository) {
                                    repository.isUsernameTaken(username)
                                } else false // fallback for other repository types
                            }

                            if (exists) {
                                Toast.makeText(
                                    this@SixthActivity,
                                    "Username already taken",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Insert user safely
                                val newUser = User(
                                    userName = username,
                                    userPassword = password.encodeBase64()
                                )

                                withContext(Dispatchers.IO) {
                                    repository.insert(newUser)
                                }

                                Toast.makeText(
                                    this@SixthActivity,
                                    "User registered!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Go back to login screen
                                startActivity(Intent(this@SixthActivity, ThirdActivity::class.java))
                                finish()
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
fun RegisterScreen(
    onRegister: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BeerGradient),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                RegisterTopBar(onBack = onBack)
            }
        ) { innerPadding ->
            // Pretty complicated looking for what it actually is
            val top = innerPadding.calculateTopPadding()
            val bottom = innerPadding.calculateBottomPadding()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = top / 2 - 10.dp, // Making it half and subtracting it with 10 makes it the same as the first login screen
                        bottom = bottom,        // keep bottom as is
                    ),
                contentAlignment = Alignment.Center   // center child
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Image(
                            painter = painterResource(R.drawable.beerstacklogo),
                            contentDescription = "BeerStack Logo",
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape) // Makes the image have a circle form
                        )

                        Text("Register", style = MaterialTheme.typography.titleLarge)

                        TextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = LoginTextFieldColors()
                        )

                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = LoginTextFieldColors()
                        )

                        TextField(
                            value = repeatPassword,
                            onValueChange = { repeatPassword = it },
                            label = { Text("Repeat Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = LoginTextFieldColors()
                        )

                        FilledTonalButton(
                            onClick = {

                                // VALIDATION
                                when {
                                    username.isBlank() -> {
                                        Toast.makeText(context, "Fill in all fields", Toast.LENGTH_SHORT).show()
                                        return@FilledTonalButton
                                    }

                                    password.isBlank() -> {
                                        Toast.makeText(context, "Fill in all fields", Toast.LENGTH_SHORT).show()
                                        return@FilledTonalButton
                                    }

                                    repeatPassword.isBlank() -> {
                                        Toast.makeText(context, "Fill in all fields", Toast.LENGTH_SHORT).show()
                                        return@FilledTonalButton
                                    }

                                    username.length < 3  -> {
                                        Toast.makeText(context, "Username must be longer then 3 characters", Toast.LENGTH_SHORT).show()
                                        return@FilledTonalButton
                                    }
                                    password.length < 3 -> {
                                        Toast.makeText(context, "Password must be longer then 3 characters", Toast.LENGTH_SHORT).show()
                                        return@FilledTonalButton
                                    }
                                    repeatPassword.length < 3 -> {
                                        Toast.makeText(context, "Password must be longer then 3 characters", Toast.LENGTH_SHORT).show()
                                        return@FilledTonalButton
                                    }
                                    password != repeatPassword -> {
                                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                        return@FilledTonalButton
                                    }

                                    else -> {
                                        onRegister(username, password)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Register")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterTopBar(onBack: () -> Unit) {
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