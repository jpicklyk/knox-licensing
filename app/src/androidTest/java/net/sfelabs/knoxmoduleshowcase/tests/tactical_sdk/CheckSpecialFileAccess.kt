package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import junit.framework.TestCase
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.io.File

@SmallTest
class CheckSpecialFileAccess {


    @Test
    fun testUsbDevicesDirectoryIsReadable() {
        //val directoryPath = "/sys/hub/usb/devices"
        val directoryPath = "/sys/bus/usb/devices"

        val directory = File(directoryPath)

        // Check if the directory exists and is readable
        assertTrue(
            "$directoryPath does not exist or is not readable",
            directory.exists() && directory.canRead()
        )
        // List the directory contents
        val fileList = directory.listFiles()
        // Print the directory contents
        fileList?.forEach { file ->
            println(file.absolutePath)
            try {
                val ueventFile = File(file, "uevent")
                assertTrue(
                    "Unable to read uevent file ${ueventFile.absolutePath}, SE Linux permission issue!",
                    ueventFile.canRead()
                )
            } catch (_: NullPointerException) {
                // Do nothing, the ueventFile doesn't exist
            }
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
