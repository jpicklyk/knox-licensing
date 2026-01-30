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
class JeffsManualUsbWhitelistTests {
    val usb_c_drive_serial = "0C9D92C19C40F62109890113"
    private val usb_c_drive_vid_pid = "0951:172B"
    val mouse_serial = null
    val mouse_vid_pid = "046D:C03D"
    private val amazon_basics_ethernet_dongle_serial = "00000000000001"
    val amazon_basics_ethernet_dongle_vid_pid = "0B95:1790"

    /** QGeem hub and ethernet
     *  Superspeed hub - 1D5C:5001
     *  Ethernet - 0BDA:8156
     */
    val usbHubVidPid = "1D5C:5001:0BDA:8156"
    val usbHubSerial = "001500A96"

    @Test
    fun whitelistHubAndEthernetByVidPid() = runTest {
        val vidPidCase = SetUsbDeviceAccessAllowedListUseCase()
        val result2 = vidPidCase.invoke(true, usbHubVidPid)
        assert(result2 is ApiResult.Success)
    }

    @Test
    fun whitelistEthernetSerialAndUsbDriveVidPid() = runTest {
        val serials = listOf(amazon_basics_ethernet_dongle_serial)
        val vidPid = usb_c_drive_vid_pid

        val serialCase = SetUsbAccessBySerialUseCase()
        val result = serialCase.invoke(true, serials)
        assert(result is ApiResult.Success)

        val vidPidCase = SetUsbDeviceAccessAllowedListUseCase()
        val result2 = vidPidCase.invoke(true, vidPid)
        assert(result2 is ApiResult.Success)

        val getSerialCase = GetUsbAccessBySerialUseCase()
        val res = getSerialCase.invoke()
        assert(res is ApiResult.Success && res.data == amazon_basics_ethernet_dongle_serial)

        val getVidPidCase = GetUsbDeviceAccessAllowedListUseCase()
        val res2 = getVidPidCase.invoke()
        assert(res2 is ApiResult.Success && res2.data == usb_c_drive_vid_pid)
    }


    @Test
    fun disableWhitelisting() = runTest {
        val result = SetUsbAccessBySerialUseCase().invoke(false, listOf("OFF"))
        assert(result is ApiResult.Success)
        val res = GetUsbAccessBySerialUseCase().invoke()
        assert(res is ApiResult.Success && res.data == "OFF")

        val result2 = SetUsbDeviceAccessAllowedListUseCase().invoke(false, "OFF")
        assert(result2 is ApiResult.Success)
        val res2 = GetUsbDeviceAccessAllowedListUseCase().invoke()
        assert(res2 is ApiResult.Success && res2.data == "OFF")
    }
}