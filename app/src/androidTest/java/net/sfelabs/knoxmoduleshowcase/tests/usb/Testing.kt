package net.sfelabs.knoxmoduleshowcase.tests.usb

import android.content.Context
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import androidx.test.platform.app.InstrumentationRegistry

class Testing {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val usbManager: UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    private fun setUpDevice(device: UsbDevice) {
        val connection: UsbDeviceConnection? = usbManager.openDevice(device)

        connection?.let {
            for (i in 0 until device.interfaceCount) {
                val usbInterface: UsbInterface = device.getInterface(i)

                // Example: Select a CDC DATA interface (class code 10)
                // This check would be from the Knox exception list
                if (usbInterface.interfaceClass == UsbConstants.USB_CLASS_CDC_DATA) {
                    if (connection.claimInterface(usbInterface, true)) {

                        // Now you can interact with the endpoints of this interface
                        val endpoint: UsbEndpoint = usbInterface.getEndpoint(0)
                        // Perform I/O operations with the endpoint

                        // After you are done, release the interface
                        connection.releaseInterface(usbInterface)
                    } else {
                        //Do something like throw an exception to the caller
                    }
                }
            }
        }
    }
/*
    private val lock = Any() // Object used for synchronization

    private fun oneShot(command: String) {
        synchronized(lock) { // Acquire lock before accessing shared resources
            try {
                var isServiceRunning: String = SystemProperties.get("service_state")
                var isTimedOut = false
                val startTime = System.currentTimeMillis()

                Log.w(TAG, "Service initially running: $isServiceRunning")

                while (isServiceRunning == "running" && !isTimedOut) {
                    isServiceRunning = SystemProperties.get("service_state")
                    Log.w(TAG, "Service still running: $isServiceRunning")

                    Thread.sleep(100)

                    if (System.currentTimeMillis() - startTime >= 1000) {
                        Log.w(TAG, "Service running timed out")
                        isTimedOut = true
                    }
                }

                if (!isTimedOut) {
                    passToOneShot(command) // This is now protected by the lock and is thread safe
                    // Indicate success
                } else {
                    // Handle timeout error
                }
            } catch (e: Exception) {
                Log.w(TAG, "executeAdbCommand - $e")
            }
        } // Release lock when exiting synchronized block
    }

 */
}