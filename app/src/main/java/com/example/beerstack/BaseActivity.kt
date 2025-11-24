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
        return checkSuPath() || checkWhichSu()
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

    private fun checkWhichSu(): Boolean {
        return try {
            Runtime.getRuntime()
                //Executes the command 'which su'
                .exec(arrayOf("which", "su"))
                //Read the output of the earlier executed command
                .inputStream
                .bufferedReader()
                .readText()
                //If it's not empty that means the device is likely rooted
                //because 'which su' doesn't return anything if there isn't a super user on the device
                .isNotEmpty()
        } catch (_: Exception) {
            //This catches any exceptions like if the command doesn't exist or shell execution is blocked
            false
        }
    }
}