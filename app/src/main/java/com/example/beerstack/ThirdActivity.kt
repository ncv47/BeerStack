package com.example.beerstack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.beerstack.data.UserDB.User
import com.example.beerstack.data.UserDB.AppDataContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


//Third Page For Login Page
class ThirdActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = AppDataContainer(this).usersRepository
        setContent {
            CoroutineScope(Dispatchers.IO).launch {
                val currentItems = repository.getAllItemsStream().firstOrNull() ?: emptyList()
                if (currentItems.isEmpty()) {
                    val users = listOf(
                        User(userName = "Marco", userPassword = "erm"),
                        User(userName = "George", userPassword = "erm")
                    )
                    users.forEach { repository.insertItem(it) }
                }
            }
        }

    }
}