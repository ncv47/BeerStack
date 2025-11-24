package com.example.beerstack

import android.os.Bundle
import androidx.activity.ComponentActivity

//New activity so I can link all others to this instead of getting an entire root check in every activity
abstract class BaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isRooted()) {
            //Closes the app
            finish()
            return
        }
    }

    private fun isRooted(): Boolean {
        return true
    }
}