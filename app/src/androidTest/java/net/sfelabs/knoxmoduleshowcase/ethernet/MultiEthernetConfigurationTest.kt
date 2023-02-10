package net.sfelabs.knoxmoduleshowcase.ethernet

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.custom.CustomDeviceManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.NetworkInterface

@RunWith(AndroidJUnit4::class)
class MultiEthernetConfigurationTest {

    lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }

    private val cdm: CustomDeviceManager = CustomDeviceManager.getInstance()
    private val systemManager = cdm.systemManager

    @Test
    fun readHardwareAddress() {
        val name = "eth0"
        try {
            println("eth0 address:")
            val eth0 = NetworkInterface.getByName("eth0")
            println(eth0.hardwareAddress)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}