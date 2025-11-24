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
        return checkSuPath()
    }

    private fun checkSuPath(): Boolean {
        //Checks for su binaries
        val paths = arrayOf(
            "/sbin/su",
            "/system/app/Superuser.apk",
            "/system/bin/failsafe/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/system/sd/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/data/local/su"
        )
        return paths.any { java.io.File(it).exists() }
    }
}