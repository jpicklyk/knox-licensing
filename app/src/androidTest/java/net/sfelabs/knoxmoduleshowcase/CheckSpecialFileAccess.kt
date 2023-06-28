package net.sfelabs.knoxmoduleshowcase

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import junit.framework.TestCase
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
@SmallTest
class CheckSpecialFileAccess {


    @Test
    fun testUsbDevicesDirectoryIsReadable() {
        //val directoryPath = "/sys/hub/usb/devices"
        val directoryPath = "/sys/bus/usb/devices"

        val directory = File(directoryPath)

        // Check if the directory exists and is readable
        TestCase.assertTrue(
            "$directoryPath does not exist or is not readable",
            directory.exists() && directory.canRead()
        )
        // List the directory contents
        val fileList = directory.listFiles()
        // Print the directory contents
        fileList?.forEach { file ->
            println(file.absolutePath)
        }
    }

    @Test
    fun testPppInitializationScriptExists() {
        //val directoryPath = "/sys/hub/usb/devices"
        val pppdScriptLocation = "/system/etc/start_knoxcustom_PMNett.sh"

        val pppdScriptFile = File(pppdScriptLocation)

        // Check if the directory exists and is readable
        TestCase.assertTrue(
            "$pppdScriptLocation does not exist or is not readable",
            pppdScriptFile.exists()
        )
    }



}