package net.sfelabs.knox_tactical.domain.linux

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.InputStreamReader

@RunWith(AndroidJUnit4::class)
@SmallTest
class CheckLinuxConfigurations {

    @Test
    fun checkConfiguration_CONFIG_PPP_BSDCOMP() {
        val config = "CONFIG_PPP_BSDCOMP=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }

    @Test
    fun checkConfiguration_CONFIG_PPP_DEFLATE() {
        val config = "CONFIG_PPP_DEFLATE=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }

    @Test
    fun checkConfiguration_CONFIG_PPP_FILTER() {
        val config = "CONFIG_PPP_FILTER=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }

    @Test
    fun checkConfiguration_CONFIG_PPP_MPPE() {
        val config = "CONFIG_PPP_MPPE=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }

    @Test
    fun checkConfiguration_CONFIG_PPP_MULTILINK() {
        val config = "CONFIG_PPP_MULTILINK=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }

    @Test
    fun checkConfiguration_CONFIG_PPPOE() {
        val config = "CONFIG_PPPOE=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }
    @Test
    fun checkConfiguration_CONFIG_PPPOL2TP() {
        val config = "CONFIG_PPPOL2TP=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }

    @Test
    fun checkConfiguration_CONFIG_PPP_ASYNC() {
        val config = "CONFIG_PPP_ASYNC=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }

    @Test
    fun checkConfiguration_CONFIG_PPP_SYNC_TTY() {
        val config = "CONFIG_PPP_SYNC_TTY=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }

    @Test
    fun checkCDCNCM_isConfigured() {
        val config = "CONFIG_USB_NET_CDC_NCM=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }

    @Test
    fun checkCDCEEM_isConfigured() {
        val config = "CONFIG_USB_NET_CDC_EEM=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }

    private fun checkLinuxConfiguration(config: String) : Boolean {
        val uiDevice: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        // Execute the adb shell command to print config.gz
        val outputString = uiDevice.executeShellCommand("zcat /proc/config.gz")
        // Read the output of the command
        val reader = BufferedReader(InputStreamReader(outputString.byteInputStream()))
        val output = StringBuilder()
        var line: String?
        var success = false
        while (reader.readLine().also { line = it } != null) {
            output.append(line).append("\n")
            if(line?.contains(config) ==true) {
                println(line)
                success = true
            }
        }
        // Close the reader
        reader.close()
        return success
    }
}