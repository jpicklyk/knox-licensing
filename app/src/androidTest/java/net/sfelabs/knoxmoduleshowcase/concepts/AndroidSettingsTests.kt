package net.sfelabs.knoxmoduleshowcase.concepts

import android.provider.Settings
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidSettingsTests {

    @Test
    fun fetch_bluetooth_name() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val result = Settings.Secure.getString(context.contentResolver, "bluetooth_name")
        println("Bluetooth Name: $result")
    }
}