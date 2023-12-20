package net.sfelabs.knoxmoduleshowcase.usb

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule.provideKnoxSystemManager
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbDeviceAccessAllowedListUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbDeviceAccessAllowedListUseCase
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 100)
class UsbDeviceAccessVidPidTests {
    private val sm = provideKnoxSystemManager()

    @Test
    fun setUsbDeviceAccessAllowedList() = runTest {
        val vidpid = "1234:4321"
        val useCase = SetUsbDeviceAccessAllowedListUseCase(sm)
        val result = useCase.invoke(true, vidpid)
        assert(result is ApiCall.Success)
        val getCase = GetUsbDeviceAccessAllowedListUseCase(sm)
        val res = getCase.invoke()
        assert(res is ApiCall.Success && res.data == vidpid)
    }

    @Test
    fun usbDeviceAccessAllowedList_DisableAndClear() = runTest {
        val vidpid = "OFF"
        val useCase = SetUsbDeviceAccessAllowedListUseCase(sm)
        val result = useCase.invoke(false, vidpid)
        assert(result is ApiCall.Success)
        val getCase = GetUsbDeviceAccessAllowedListUseCase(sm)
        val res = getCase.invoke()
        assert(res is ApiCall.Success && res.data == vidpid)
    }

    @Test
    fun usbDeviceAccessAllowedList_Disable() = runTest {
        val vidpid = "1234:4321"
        val useCase = SetUsbDeviceAccessAllowedListUseCase(sm)
        val result = useCase.invoke(false, vidpid)
        assert(result is ApiCall.Success)
        val getCase = GetUsbDeviceAccessAllowedListUseCase(sm)
        val res = getCase.invoke()
        assert(res is ApiCall.Success && res.data == "OFF")
    }


}