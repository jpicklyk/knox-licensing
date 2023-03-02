package net.sfelabs.knoxmoduleshowcase.concepts

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This class is here to serve as sample business logic that needs to be used to cover the use cases
 * required for the PID:VID/Serial number allow listing to work correctly.  This is internally
 * implemented in the Knox framework
 */
@RunWith(AndroidJUnit4::class)
class UsbAllowListConcept {

    @Test
    fun allUsbConnectionsAllowed() {
        val device = UsbDescriptor("0B95", "1790")
        val deviceAllowed = isUsbConnectionAllowed(device)
        assert(deviceAllowed)
    }

    @Test
    fun vidPidAllowListDeviceAllowed() {
        val device = UsbDescriptor("0B95", "1790", "12345")
        val vidPidAllowList: List<String> = listOf("0B95:1790", "9A21:1234")
        val deviceAllowed = isUsbConnectionAllowed(device, emptyList(), vidPidAllowList)
        assert(deviceAllowed)
    }

    @Test
    fun vidPidAllowListDeviceBlocked() {
        val device = UsbDescriptor("1234", "5678", "12345")
        val vidPidAllowList: List<String> = listOf("0B95:1790", "9A21:1234")
        val deviceAllowed = isUsbConnectionAllowed(device, emptyList(), vidPidAllowList)
        assert(!deviceAllowed)
    }

    @Test
    fun serialNumberAllowListDeviceAllowed() {
        val serialAllowList: List<String> = listOf("123456789", "12345")
        val device = UsbDescriptor("0B95", "1790", "123456789")
        val deviceAllowed = isUsbConnectionAllowed(device, serialAllowList)
        assert(deviceAllowed)
    }

    @Test
    fun serialNumberAllowListDeviceBlocked() {
        val serialAllowList: List<String> = listOf("123456789", "12345")
        val device = UsbDescriptor("0B95", "1790", "012345678")
        val deviceAllowed = isUsbConnectionAllowed(device, serialAllowList)
        assert(!deviceAllowed)
    }

    @Test
    fun serialNumberAllowListDeviceSerialNullBlocked() {
        val serialAllowList: List<String> = listOf("123456789")
        val device = UsbDescriptor("0B95", "1790", null)
        val deviceAllowed = isUsbConnectionAllowed(device, serialAllowList)
        assert(!deviceAllowed)
    }

    @Test
    fun serialNumberAndVidPidAllowListDeviceVidPidNoSerialAllowed() {
        val serialAllowList: List<String> = listOf("123456789")
        val vidPidAllowList: List<String> = listOf("0B95:1790")
        val device = UsbDescriptor("0B95", "1790", null)
        val deviceAllowed = isUsbConnectionAllowed(device, serialAllowList, vidPidAllowList)
        assert(deviceAllowed)
    }

    @Test
    fun serialNumberAndVidPidAllowListDeviceVidPidNoSerialBlocked() {
        val serialAllowList: List<String> = listOf("123456789")
        val vidPidAllowList: List<String> = listOf("0B95:1790")
        val device = UsbDescriptor("0A95", "2790", null)
        val deviceAllowed = isUsbConnectionAllowed(device, serialAllowList, vidPidAllowList)
        assert(!deviceAllowed)
    }

    @Test
    fun serialNumberAndVidPidAllowListDeviceVidPidSerialAllowed() {
        val serialAllowList: List<String> = listOf("123456789")
        val vidPidAllowList: List<String> = listOf("0B95:1790")
        val device = UsbDescriptor("0B95", "1790", "012345678")
        val deviceAllowed = isUsbConnectionAllowed(device, serialAllowList, vidPidAllowList)
        assert(deviceAllowed)
    }

    @Test
    fun serialNumberAndVidPidAllowListDeviceVidPidSerialBlocked() {
        val serialAllowList: List<String> = listOf("123456789")
        val vidPidAllowList: List<String> = listOf("0B95:1790")
        val device = UsbDescriptor("0A95", "2790", "012345678")
        val deviceAllowed = isUsbConnectionAllowed(device, serialAllowList, vidPidAllowList)
        assert(!deviceAllowed)
    }

    private fun isUsbConnectionAllowed(
        usbDescriptor: UsbDescriptor,
        serialAllowList: List<String> = emptyList(),
        vidPidAllowList: List<String> = emptyList()
    ) : Boolean {
        /**
         * Most simplistic scenario where there is no defined whitelist.  In this case allow all
         * connections.
         */
        return if(serialAllowList.isEmpty() && vidPidAllowList.isEmpty())
            true

        /**
         * Check the most restrictive use case of serial number whitelisting next
         */
        else if(serialAllowList.isNotEmpty()) {
            if(usbDescriptor.hasSerial() && serialAllowList.contains(usbDescriptor.serialNumber))
                true
            else vidPidAllowList.isNotEmpty() && vidPidAllowList.contains(usbDescriptor.vidPidString())
        }

        /**
         * Check the VID:PID whitelisting
         */
        else vidPidAllowList.isNotEmpty() && vidPidAllowList.contains(usbDescriptor.vidPidString())
    }

    data class UsbDescriptor(val vid: String, val pid: String, val serialNumber: String? = null) {
        fun hasSerial(): Boolean {
            if(serialNumber == null)
                return false
            return true
        }

        fun vidPidString(): String {
            return "${vid}:${pid}"
        }
    }
}