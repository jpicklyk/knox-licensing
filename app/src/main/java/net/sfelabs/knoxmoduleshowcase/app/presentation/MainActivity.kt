package net.sfelabs.knoxmoduleshowcase.app.presentation

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager: DevicePolicyManager =
            getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if(manager.isDeviceOwnerApp(applicationContext.packageName)) {
            Log.d(TAG, "The application is the device owner")
        } else {
            Log.d(TAG, "The application is not the device owner")
        }
        setContent {
            KnoxShowcaseApp()
        }
    }

}