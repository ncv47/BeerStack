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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.beerstack.data.UserDB.OfflineUsersRepository
import io.ktor.util.encodeBase64

class SixthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AppDataContainer(this).usersRepository

        setContent {
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
                }
            )
        }
    }
}


@Composable
fun RegisterScreen(
    onRegister: (String, String) -> Unit
) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Image(
                    painter = painterResource(R.drawable.beerstacklogo),
                    contentDescription = "BeerStack Logo",
                    modifier = Modifier.size(96.dp)
                )

                Text("Registereeeee", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = repeatPassword,
                    onValueChange = { repeatPassword = it },
                    label = { Text("Repeat Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
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
