package net.sfelabs.knoxmoduleshowcase.manual_tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbAccessBySerialUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbDeviceAccessAllowedListUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbAccessBySerialUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbDeviceAccessAllowedListUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LargeUsbWhitelistTests {

    /**
     * Generates a list of 100 VID:PID entries in the format XXXX:XXXX
     * VIDs range from 0001 to 0064 (100 entries)
     * PIDs range from 1000 to 1063 (100 entries)
     */
    private fun generate100VidPidEntries(): String {
        return (1..100).joinToString(":") { index ->
            val vid = String.format("%04X", index)
            val pid = String.format("%04X", 1000 + index - 1)
            "$vid:$pid"
        }
    }

    /**
     * Generates a list of 100 serial numbers
     * Format: SERIAL_XXXXX where XXXXX is zero-padded number from 00001 to 00100
     */
    private fun generate100SerialEntries(): List<String> {
        return (1..100).map { index ->
            "SERIAL_${String.format("%05d", index)}"
        }
    }

    @Test
    fun testWrite100VidPidEntries() = runTest {
        val vidPidEntries = generate100VidPidEntries()

        // Set the VID:PID whitelist
        val setUseCase = SetUsbDeviceAccessAllowedListUseCase()
        val setResult = setUseCase.invoke(true, vidPidEntries)

        // Verify the set operation succeeded
        assert(setResult is ApiResult.Success) {
            "Failed to set VID:PID whitelist: $setResult"
        }

        // Get the VID:PID whitelist back
        val getUseCase = GetUsbDeviceAccessAllowedListUseCase()
        val getResult = getUseCase.invoke()

        // Verify the get operation succeeded and returned the same data
        assert(getResult is ApiResult.Success) {
            "Failed to get VID:PID whitelist: $getResult"
        }

        if (getResult is ApiResult.Success) {
            assert(getResult.data == vidPidEntries) {
                "VID:PID mismatch!\nExpected: $vidPidEntries\nActual: ${getResult.data}"
            }
        }
    }

    @Test
    fun testWrite100SerialEntries() = runTest {
        val serialEntries = generate100SerialEntries()

        // Set the serial whitelist
        val setUseCase = SetUsbAccessBySerialUseCase()
        val setResult = setUseCase.invoke(true, serialEntries)

        // Verify the set operation succeeded
        assert(setResult is ApiResult.Success) {
            "Failed to set serial whitelist: $setResult"
        }

        // Get the serial whitelist back
        val getUseCase = GetUsbAccessBySerialUseCase()
        val getResult = getUseCase.invoke()

        // Verify the get operation succeeded and returned the same data
        assert(getResult is ApiResult.Success) {
            "Failed to get serial whitelist: $getResult"
        }

        if (getResult is ApiResult.Success) {
            // The API returns serials as a colon-separated string
            val expectedSerialString = serialEntries.joinToString(":")
            assert(getResult.data == expectedSerialString) {
                "Serial mismatch!\nExpected (as string): $expectedSerialString\nActual: ${getResult.data}"
            }
        }
    }

    @Test
    fun testWriteBoth100VidPidAnd100SerialEntries() = runTest {
        val vidPidEntries = generate100VidPidEntries()
        val serialEntries = generate100SerialEntries()

        // Set the VID:PID whitelist
        val setVidPidUseCase = SetUsbDeviceAccessAllowedListUseCase()
        val setVidPidResult = setVidPidUseCase.invoke(true, vidPidEntries)
        assert(setVidPidResult is ApiResult.Success) {
            "Failed to set VID:PID whitelist: $setVidPidResult"
        }

        // Set the serial whitelist
        val setSerialUseCase = SetUsbAccessBySerialUseCase()
        val setSerialResult = setSerialUseCase.invoke(true, serialEntries)
        assert(setSerialResult is ApiResult.Success) {
            "Failed to set serial whitelist: $setSerialResult"
        }

        // Get the VID:PID whitelist back
        val getVidPidUseCase = GetUsbDeviceAccessAllowedListUseCase()
        val getVidPidResult = getVidPidUseCase.invoke()
        assert(getVidPidResult is ApiResult.Success) {
            "Failed to get VID:PID whitelist: $getVidPidResult"
        }

        if (getVidPidResult is ApiResult.Success) {
            assert(getVidPidResult.data == vidPidEntries) {
                "VID:PID mismatch!\nExpected: $vidPidEntries\nActual: ${getVidPidResult.data}"
            }
        }

        // Get the serial whitelist back
        val getSerialUseCase = GetUsbAccessBySerialUseCase()
        val getSerialResult = getSerialUseCase.invoke()
        assert(getSerialResult is ApiResult.Success) {
            "Failed to get serial whitelist: $getSerialResult"
        }

        if (getSerialResult is ApiResult.Success) {
            val expectedSerialString = serialEntries.joinToString(":")
            assert(getSerialResult.data == expectedSerialString) {
                "Serial mismatch!\nExpected (as string): $expectedSerialString\nActual: ${getSerialResult.data}"
            }
        }
    }

    @Test
    fun disableWhitelisting() = runTest {
        // Disable serial whitelisting
        val result = SetUsbAccessBySerialUseCase().invoke(false, listOf("OFF"))
        assert(result is ApiResult.Success) {
            "Failed to disable serial whitelisting: $result"
        }
        val res = GetUsbAccessBySerialUseCase().invoke()
        assert(res is ApiResult.Success && res.data == "OFF") {
            "Serial whitelist not disabled properly: $res"
        }

        // Disable VID:PID whitelisting
        val result2 = SetUsbDeviceAccessAllowedListUseCase().invoke(false, "OFF")
        assert(result2 is ApiResult.Success) {
            "Failed to disable VID:PID whitelisting: $result2"
        }
        val res2 = GetUsbDeviceAccessAllowedListUseCase().invoke()
        assert(res2 is ApiResult.Success && res2.data == "OFF") {
            "VID:PID whitelist not disabled properly: $res2"
        }
    }
}
