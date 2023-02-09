package net.sfelabs.knoxmoduleshowcase.pppd

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.runBlocking
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_common.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import net.sfelabs.knox_tactical.domain.use_cases.adb.StopPppdUseCase
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.File
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
class PppdTtyAcmTests {
    private val tag = "TTYACMTEST"
    private lateinit var context: Context

    @Rule
    @JvmField
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule
        .grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
        /*val file = File("/sdcard/options")
        FileOutputStream(file).use { out ->
            context.assets.open("options").use {
                it.copyTo(out)
            }
        }*/
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

        val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.PPPD, "/dev/ttyACM0 file /sdcard/options")
        //val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.PPPD, "/dev/ttyUSB0 file /sdcard/options")
        assert(result is ApiCall.Success)
    }

    @Test
    fun testPpp0InterfaceExists() {
        //Not the best way to handle the situation but good enough for now.  Need to wait for
        // interface availability which takes a couple of seconds.
        Thread.sleep(3000)
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            if(interfaces == null) {
                Log.e(tag, "Unable to retrieve any network interfaces")
            } else {
                var count = 0
                while (interfaces.hasMoreElements()) {
                    val ne = interfaces.nextElement()
                    if (ne.displayName.startsWith("ppp")) {
                        count++
                        val message = StringBuilder("------------------\n")
                        message.append("Checking interface: ${ne.displayName}\n")
                        //Causes SocketException when looking at ppp interface
                        //message.append("Is up? ${ ne?.isUp }\n")
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
                    assert(false)
                }
                assert(true)
            }
        } catch (e: Exception) {
            Log.e(tag, e.message, e)
            assert(false)
        }
    }

    @Test
    fun testStopPppd() = runBlocking<Unit> {
        val sm = KnoxModule.provideKnoxSystemManager()
        val useCase = StopPppdUseCase(sm)
        val result = useCase.invoke()
        assert(result is ApiCall.Success)
    }
}