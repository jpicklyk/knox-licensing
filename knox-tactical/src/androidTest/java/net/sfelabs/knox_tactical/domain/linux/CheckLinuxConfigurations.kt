package net.sfelabs.knox_tactical.domain.linux

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
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
    fun checkCDCECM_isConfigured() {
        val config = "CONFIG_USB_CONFIGFS_ECM_SUBSET=y"
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

    @Test
    fun checkASIX_A_isConfigured() {
        var config = "CONFIG_USB_NET_AX8817X=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )

        config = "CONFIG_USB_NET_AX88179_178A=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )

        config = "CONFIG_USB_NET_AX88178=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkASIX_B_isConfigured() {
        //prerequisite for the USB driver
        val config = "CONFIG_AX88796B_PHY=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }

    /**
     * The XC6P already has this device supported however it is not on the S23TE Linux build.
     */
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun check_lan78xx_isConfigured() {
        //prerequisite for the USB driver
        val config = "CONFIG_USB_LAN78XX=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun check_rtl8153_ecm_isConfigured() {
        //prerequisite for the USB driver
        val config = "CONFIG_USB_RTL8153_ECM=y"
        Assert.assertTrue(
            "Linux configuration '$config' is not set!",
            checkLinuxConfiguration(config)
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun check_usb_net_drivers_areConfigured() {
        //prerequisite for the USB driver
        val config1 = "CONFIG_USB_NET_SMSC75XX=y"
        Assert.assertTrue(
            "Linux configuration '$config1' is not set!",
            checkLinuxConfiguration(config1)
        )
        val config2 = "CONFIG_USB_NET_SMSC95XX=y"
        Assert.assertTrue(
            "Linux configuration '$config2' is not set!",
            checkLinuxConfiguration(config2)
        )
        val config3 = "CONFIG_USB_NET_AQC111=y"
        Assert.assertTrue(
            "Linux configuration '$config3' is not set!",
            checkLinuxConfiguration(config3)
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