package com.example.beerstack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.beerstack.data.UserDB.User
import com.example.beerstack.data.BeerDB.AppDataContainer
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

class SixthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AppDataContainer(this).usersRepository

        setContent {
            RegisterScreen(
                onRegister = { username, password ->
                    lifecycleScope.launch {

                        // insert user safely
                        val newUser = User(
                            userName = username,
                            userPassword = password
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
            )
        }
    }
}

@Composable
fun RegisterScreen(
    onRegister: (String, String) -> Unit
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
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Image(
                    painter = painterResource(R.drawable.beerstacklogo),
                    contentDescription = "BeerStack Logo",
                    modifier = Modifier.size(96.dp)
                )

                Text("Register", style = MaterialTheme.typography.titleLarge)

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
                    onClick = { onRegister(username, password) },
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
