package net.sfelabs.knoxmoduleshowcase

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import junit.framework.TestCase
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

@RunWith(AndroidJUnit4::class)
@SmallTest
class CheckSpecialFileAccess {

    @Test
    fun checkCDCNCM_isConfigured() {
        val cdcNcm = "CONFIG_USB_NET_CDC_NCM=y"
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
            if(line?.equals(cdcNcm) ==true)
                success = true
        }
        // Close the reader
        reader.close()
        assert(success)
    }

    @Test
    fun checkCDCEEM_isConfigured() {
        val cdcEem = "CONFIG_USB_NET_CDC_EEM=y"
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
            if(line?.equals(cdcEem) ==true)
                success = true
        }
        // Close the reader
        reader.close()
        assert(success)
    }

    @Test
    fun testUsbDevicesDirectoryIsReadable() {
        val directoryPath = "/sys/hub/usb/devices"

        val directory = File(directoryPath)

        // Check if the directory exists and is readable
        TestCase.assertTrue(
            "$directoryPath does not exist or is not readable",
            directory.exists() && directory.canRead()
        )

        // List the directory contents
        val fileList = directory.listFiles()

        // Check if the directory is not empty
        //TestCase.assertTrue("$directoryPath is empty", fileList.isNotEmpty())

        // Print the directory contents
        fileList?.forEach { file ->
            println(file.absolutePath)
        }
    }

}