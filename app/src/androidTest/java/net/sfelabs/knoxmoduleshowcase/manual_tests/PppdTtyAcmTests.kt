package net.sfelabs.knoxmoduleshowcase.manual_tests

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import net.sfelabs.knox_tactical.domain.use_cases.adb.StopPppdUseCase
import net.sfelabs.knoxmoduleshowcase.R
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.File
import java.io.FileOutputStream
import java.net.NetworkInterface

/**
 * This test class requires that the device be setup to communicate over a null modem cable in the
 * ttyACM mode.  Having the connections the wrong way will setup ttyUSB0 instead of ACM.  For testing
 * please ensure you have copied the options file in the res folder to /sdcard/options to pass to the
 * ppp command.
 *
 * Tests will be run in ascending order based on the function name.  Be careful inserting new tests
 * and ensure naming is correctly ordered to properly clean up or undo changes.
 */

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@TacticalSdkSuppress(minReleaseVersion = 110)
class PppdTtyAcmTests {
    private val tag = "TTYACMTEST"
    private lateinit var context: Context
    private lateinit var optionsFileLocation: File
//
//    @get:Rule
//    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule
//        .grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val targetDirectory = context.getExternalFilesDir(null)!!
        optionsFileLocation = copyFileFromRawToSDCard(
            context, R.raw.options, targetDirectory, "options"
        )
    }

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
                println("Can read $ueventFile : ${ueventFile.canRead()}")
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
    fun checkForTtyACM0() {
        val acmFile = File("/dev/ttyACM0")
        assert(acmFile.exists())
    }

    @Test
    fun checkThatTtyUSB0DoesNotExist() {
        val usbFile = File("/dev/ttyUSB0")
        assert(!usbFile.exists())
    }

    @Test
    fun setupPpp() = runBlocking<Unit> {
        val useCase = ExecuteAdbCommandUseCase()
        val optionsFile = File("/sdcard/options")
        assertTrue("Options file was not copied to /sdcard/options!",optionsFile.exists())
        //KnoxCustomManagerService: executeAdbCommand - java.lang.IllegalArgumentException: value of system property 'knoxsdk.tac.body' is longer than 91 bytes: /dev/ttyUSB0 file /storage/emulated/0/Android/data/net.sfelabs.knoxmoduleshowcase/files/options
        //val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.PPPD, "/dev/ttyACM0 file $optionsFileLocation")
        val result = useCase.invoke(AdbHeader.PPPD, "/dev/ttyACM0 file /sdcard/options")
        //val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.PPPD, "/dev/ttyUSB0 file /sdcard/options")
        assert(result is ApiResult.Success)
    }

    @Test
    fun testPpp0InterfaceExists() {
        //Not the best way to handle the situation but good enough for now.  Need to wait for
        // interface availability which takes a couple of seconds.
        Thread.sleep(5000)
        try {
            assertTrue("Interface ppp0 was not found", isPpp0InterfaceUp())
        } catch (e: Exception) {
            Log.e(tag, e.message, e)
            assertTrue(e.message,false)
        }
    }


    @Test
    fun testStopPppd() = runBlocking<Unit> {
        Thread.sleep(1000)
        val useCase = StopPppdUseCase()
        val result = useCase.invoke()
        delay(1000)
        assertTrue("Knox API stopPPPD() was not successful.  $result", result is ApiResult.Success)
        assertFalse(
            "stopPPPD Api did not stop service.  Interface PPP0 is still available!",
            isPpp0InterfaceUp()
        )
    }

    //@Test
    fun testTtyACMDevicesComprehensive() = runBlocking<Unit> {
        val tag = "TtyACMTest"

        // Test each ACM device from 0 to 4
        for (acmIndex in 0..4) {
            val acmDevice = "/dev/ttyACM$acmIndex"
            val usbDevice = "/dev/ttyUSB$acmIndex"

            Log.i(tag, "Testing ACM device: $acmDevice")

            try {
                // 1. Check that ttyACM exists
                val acmFile = File(acmDevice)
                assertTrue("$acmDevice file does not exist!", acmFile.exists())
                Log.i(tag, "$acmDevice exists")

                // 2. Check that corresponding ttyUSB does NOT exist
                val usbFile = File(usbDevice)
                assertFalse("$usbDevice should not exist!", usbFile.exists())
                Log.i(tag, "$usbDevice correctly does not exist")

                // 3. Setup PPP connection
                val setupUseCase = ExecuteAdbCommandUseCase()
                val optionsFile = File("/sdcard/options")
                assertTrue("Options file was not copied to /sdcard/options!", optionsFile.exists())

                Log.i(tag, "Setting up PPP connection on $acmDevice")
                val setupResult = setupUseCase.invoke(AdbHeader.PPPD, "$acmDevice file /sdcard/options")
                assertTrue("PPP setup failed for $acmDevice: $setupResult", setupResult is ApiResult.Success)
                Log.i(tag, "PPP setup successful for $acmDevice")

                // 4. Wait for interface to be available and test ppp0 interface
                Log.i(tag, "Waiting for ppp0 interface to become available...")
                Thread.sleep(5000)

                try {
                    assertTrue("Interface ppp0 was not found for $acmDevice", isPpp0InterfaceUp())
                    Log.i(tag, "ppp0 interface is up for $acmDevice")
                } catch (e: Exception) {
                    Log.e(tag, "ppp0 interface check failed for $acmDevice: ${e.message}", e)
                    assertTrue("ppp0 interface error for $acmDevice: ${e.message}", false)
                }

                // 5. Stop the PPP daemon
                Log.i(tag, "Stopping PPPD for $acmDevice")
                Thread.sleep(1000)
                val stopUseCase = StopPppdUseCase()
                val stopResult = stopUseCase.invoke()
                delay(1000)

                assertTrue("Knox API stopPPPD() was not successful for $acmDevice: $stopResult",
                    stopResult is ApiResult.Success)
                assertFalse("stopPPPD Api did not stop service for $acmDevice. Interface PPP0 is still available!",
                    isPpp0InterfaceUp())
                Log.i(tag, "PPPD successfully stopped for $acmDevice")

                Log.i(tag, "All tests passed for $acmDevice")

            } catch (e: Exception) {
                Log.e(tag, "Test failed for $acmDevice: ${e.message}", e)
                throw AssertionError("Test suite failed for $acmDevice: ${e.message}", e)
            }

            // Brief pause between ACM device tests
            delay(2000)
        }

        Log.i(tag, "All ttyACM devices (0-4) tested successfully!")
    }

    private fun copyFileFromRawToSDCard(
        context: Context,
        rawResourceId: Int,
        targetDirectory: File,
        targetFileName: String
    ) : File{
        val targetFile = File(targetDirectory, targetFileName)
        context.resources.openRawResource(rawResourceId).use { inputStream ->
            FileOutputStream(targetFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return targetFile
    }

    private fun isPpp0InterfaceUp(): Boolean {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        if(interfaces == null) {
            Log.e(tag, "Unable to retrieve any network interfaces")
            return false
        } else {
            var count = 0
            while (interfaces.hasMoreElements()) {
                val ne = interfaces.nextElement()
                if (ne.displayName.startsWith("ppp", true)) {
                    count++
                    val message = StringBuilder("------------------\n")
                    message.append("Checking interface: ${ne.displayName}\n")
                    message.append("IP addresses assigned to this interface are:\n")
                    val addresses = ne.inetAddresses
                    while (addresses.hasMoreElements()) {
                        message.append(addresses.nextElement().toString()+"\n")
                    }

                    message.append("------------------")
                    Log.d(tag, message.toString())
                }
            }
            if(count == 0) {
                Log.d(tag, "No PPP interfaces connected")
                return false
            }
            return true
        }
    }
}