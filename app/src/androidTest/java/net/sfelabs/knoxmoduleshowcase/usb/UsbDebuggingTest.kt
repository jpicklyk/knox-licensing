package net.sfelabs.knoxmoduleshowcase.usb

import android.Manifest
import android.app.Instrumentation
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsbDebuggingTest {
    private lateinit var context: Context
    private lateinit var usbManager: UsbManager

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
        usbManager = context.getSystemService(UsbManager::class.java)
    }

    //@Rule
    //val usbPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACT)

//    This test works but breaks debugging and makes it very difficult to recover.  Leaving the
//    test here but commented out for historical keeping.
//    @Test
//    fun disableUsbDebugging() = runTest {
//        val edm = KnoxModule.provideKnoxEnterpriseDeviceManager(context)
//        val useCase =
//            net.sfelabs.knox_tactical.domain.use_cases.tactical.adb.SetUsbDebuggingUseCase(edm)
//        val result = useCase.invoke(false)
//        assert(result is ApiCall.Success)
//    }

    @Test
    fun printAllUsbDeviceSerial() {
        val devices = usbManager.deviceList

        val permissionIntent = PendingIntent.getBroadcast(
            InstrumentationRegistry.getInstrumentation().targetContext,
            0,
            Intent(Intent.ACTION_USB),
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        if(devices.isEmpty())
            println("No devices connected")
        else {
            for (device in devices.values) {
                if(usbManager.hasPermission(device)) {
                    println("Serial number: ${device.serialNumber}")
                } else {
                    usbManager.requestPermission(device, permissionIntent)

                    val result = InstrumentationRegistry.getInstrumentation().waitForMonitorWithTimeout(
                        Instrumentation.ActivityMonitor(permissionIntent), 5000
                    )
                    println("Serial number: ${device.serialNumber}")

                }

            }
        }

    }
}