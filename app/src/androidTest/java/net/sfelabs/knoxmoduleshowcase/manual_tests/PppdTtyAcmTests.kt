package net.sfelabs.knoxmoduleshowcase.manual_tests

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_common.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import net.sfelabs.knox_tactical.domain.use_cases.adb.StopPppdUseCase
import net.sfelabs.knoxmoduleshowcase.R
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
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

    @Rule
    @JvmField
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule
        .grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val targetDirectory = context.getExternalFilesDir(null)!!
        optionsFileLocation = copyFileFromRawToSDCard(
            context, R.raw.options, targetDirectory, "options"
        )
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
        val sm = KnoxModule.provideKnoxSystemManager()
        val useCase =
            ExecuteAdbCommandUseCase(
                sm
            )
        println("options file location: $optionsFileLocation")
        assertTrue("Options file was not copied to device!",optionsFileLocation.exists())
        //KnoxCustomManagerService: executeAdbCommand - java.lang.IllegalArgumentException: value of system property 'knoxsdk.tac.body' is longer than 91 bytes: /dev/ttyUSB0 file /storage/emulated/0/Android/data/net.sfelabs.knoxmoduleshowcase/files/options
        //val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.PPPD, "/dev/ttyACM0 file $optionsFileLocation")
        val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.PPPD, "/dev/ttyACM0 file /sdcard/options")
        //val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.PPPD, "/dev/ttyUSB0 file /sdcard/options")
        assert(result is ApiCall.Success)
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
        val sm = KnoxModule.provideKnoxSystemManager()
        val useCase = StopPppdUseCase(sm)
        val result = useCase.invoke()
        assertTrue("Knox API stopPPPD() was not successful.  $result", result is ApiCall.Success)
        assertFalse(
            "stopPPPD Api did not stop service.  Interface PPP0 is still available!",
            isPpp0InterfaceUp()
        )
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