package net.sfelabs.knoxmoduleshowcase.tests.usb

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.CustomDeviceManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbDeviceAccessAllowedListUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbDeviceAccessAllowedListUseCase
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 100)
class UsbDeviceAccessVidPidTests {

    @Test
    fun setUsbDeviceAccessAllowedList() = runTest {
        val vidpid = "1234:4321"
        val useCase = SetUsbDeviceAccessAllowedListUseCase()
        val result = useCase.invoke(true, vidpid)
        assert(result is ApiResult.Success)
        val getCase = GetUsbDeviceAccessAllowedListUseCase()
        val res = getCase.invoke()
        assert(res is ApiResult.Success && res.data == vidpid)
    }

    @Test
    fun usbDeviceAccessAllowedList_DisableAndClear() = runTest {
        val vidpid = "OFF"
        val useCase = SetUsbDeviceAccessAllowedListUseCase()
        val result = useCase.invoke(false, vidpid)
        assert(result is ApiResult.Success)
        val getCase = GetUsbDeviceAccessAllowedListUseCase()
        val res = getCase.invoke()
        assert(res is ApiResult.Success && res.data == vidpid)
    }

    @Test
    fun usbDeviceAccessAllowedList_Disable() = runTest {
        val vidpid = "1234:4321"
        val useCase = SetUsbDeviceAccessAllowedListUseCase()
        val result = useCase.invoke(false, vidpid)
        assert(result is ApiResult.Success)
        val getCase = GetUsbDeviceAccessAllowedListUseCase()
        val res = getCase.invoke()
        assert(res is ApiResult.Success && res.data == "OFF")
    }


}