package com.example.beerstack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.beerstack.data.UserDB.User
import com.example.beerstack.data.BeerDB.AppDataContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.withContext

class ThirdActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = AppDataContainer(this).usersRepository

        // Compose UI for nicer, centered login screen
        setContent {
            LoginScreen(
                onLogin = { username, password ->
                    lifecycleScope.launch {
                        // Switch to IO for DB call, then back to Main automatically
                        val user = withContext(Dispatchers.IO) {
                            repository.login(username, password)
                        }

                        if (user != null) {
                            Toast.makeText(
                                applicationContext,
                                "Welcome ${user.userName}",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(
                                Intent(this@ThirdActivity, MainActivity::class.java)
                            )
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Invalid credentials",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        }

        // Populate users
        lifecycleScope.launch {
            val currentUsers = repository.getAllUsers().firstOrNull() ?: emptyList()
            if (currentUsers.isEmpty()) {
                val users = listOf(
                    User(userName = "Kenzo", userPassword = "password"),
                    User(userName = "Noah", userPassword = "password"),
                    User(userName = "CHANG", userPassword = "password"),
                    User(userName = "Lancelot", userPassword = "password")
                )
                users.forEach { repository.insert(it) }
            }
        }

    }
}

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Big logo at the top
                Image(
                    painter = painterResource(R.drawable.beerstacklogo),
                    contentDescription = "BeerStack Logo",
                    modifier = Modifier.size(96.dp)
                )

                Text(
                    text = "Login",
                    style = MaterialTheme.typography.titleLarge
                )

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

                FilledTonalButton(
                    onClick = { onLogin(username, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Login")
                }
            }
        }
    }
}
