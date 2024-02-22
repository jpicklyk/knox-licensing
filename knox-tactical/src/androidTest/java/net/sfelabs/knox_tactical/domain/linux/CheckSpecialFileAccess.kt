package net.sfelabs.knox_tactical.domain.linux

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

    /*
    Permissions should be crw rw rw

    @Test
    fun testPppPermissionsAreCorrect() {
//        val pppFile = Path("/dev/ppp")
//        val permissions = pppFile.getPosixFilePermissions()
//        println(permissions)
        val command = "ls -l /dev/ppp"
        val result = executeAdbShellCommand(command)
        println(result)

    }
    private fun isPermissiveModeEnabled(): Boolean {
        val command = "getenforce"
        val result = executeAdbShellCommand(command)
        return result.trim().equals("Permissive", ignoreCase = true)
    }


    private fun executeAdbShellCommand(command: String): String {
        val adbExecutable = findAdbExecutable()
        if (adbExecutable != null) {
            val adbCommand = "$adbExecutable shell $command"
            return executeShellCommand(adbCommand)
        }
        return "ADB executable not found."
    }
    private fun executeShellCommand(command: String): String {
        val output = StringBuilder()

        try {
            val process = ProcessBuilder()
                .command("sh", "-c", command)
                .redirectErrorStream(true)
                .start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line)
                output.append("\n")
            }

            process.waitFor()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return output.toString()
    }

    private fun findAdbExecutable(): String? {
        val paths = System.getenv("PATH")?.split(":") ?: return null
        for (path in paths) {
            val adbExecutable = "$path/adb"
            if (isExecutable(adbExecutable)) {
                return adbExecutable
            }
        }
        return null
    }

    private fun isExecutable(filePath: String): Boolean {
        val process = ProcessBuilder()
            .command("sh", "-c", "[ -x \"$filePath\" ]")
            .redirectErrorStream(true)
            .start()
        process.waitFor()
        return process.exitValue() == 0
    }
*/
}