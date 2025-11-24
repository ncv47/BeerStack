package com.example.beerstack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.beerstack.data.UserDB.User
import com.example.beerstack.data.UserDB.AppDataContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ThirdActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = AppDataContainer(this).usersRepository

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 200, 60, 60)
        }

        val etUsername = EditText(this).apply {
            hint = "Username"
        }
        val etPassword = EditText(this).apply {
            hint = "Password"
        }
        val btnLogin = Button(this).apply {
            text = "Login"
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                val user = repository.login(username, password)
                runOnUiThread {
                    if (user != null) {
                        Toast.makeText(applicationContext, "Welcome ${user.userName}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ThirdActivity, MainActivity::class.java))
                    } else {
                        Toast.makeText(applicationContext, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        layout.addView(etUsername)
        layout.addView(etPassword)
        layout.addView(btnLogin)
        setContentView(layout)

        // Populate users
        CoroutineScope(Dispatchers.IO).launch {
            val currentItems = repository.getAllItemsStream().firstOrNull() ?: emptyList()
            if (currentItems.isEmpty()) {
                val users = listOf(
                    User(userName = "Kenzo", userPassword = "password"),
                    User(userName = "Noah", userPassword = "password"),
                    User(userName = "CHANG", userPassword = "password"),
                    User(userName = "Lancelot", userPassword = "password")
                )
                users.forEach { repository.insertItem(it) }
            }
        }
    }
}
