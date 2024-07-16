package net.sfelabs.knoxmoduleshowcase.tests.usb

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbAccessBySerialUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbDeviceAccessAllowedListUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbAccessBySerialUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbDeviceAccessAllowedListUseCase
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 130)
class UsbDeviceAccessTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()

    @Test
    fun setUsbAccessBySerial_SingleSerialNumber() = runTest {
        val serials = listOf("0504a2635")
        val useCase = SetUsbAccessBySerialUseCase(systemManager)
        val result = useCase.invoke(true, serials)
        assert(result is ApiResult.Success)
        val getCase = GetUsbAccessBySerialUseCase(systemManager)
        val res = getCase.invoke()
        assert(res is ApiResult.Success && res.data == "0504a2635")
    }

    @Test
    fun setUsbAccessBySerial_MultipleSerialNumber() = runTest {
        val serials = listOf("123456789abc", "cba987654321")
        val useCase = SetUsbAccessBySerialUseCase(systemManager)
        val result = useCase.invoke(true, serials)
        assert(result is ApiResult.Success)
        val getCase = GetUsbAccessBySerialUseCase(systemManager)
        val res = getCase.invoke()
        assert(res is ApiResult.Success && res.data == "123456789abc:cba987654321")
    }

    @Test
    fun setUsbAccessBySerial_Disable() = runTest {
        val serials = listOf("123456789abc", "cba987654321")
        val useCase = SetUsbAccessBySerialUseCase(systemManager)
        val result = useCase.invoke(false, serials)
        assert(result is ApiResult.Success)
        val getCase = GetUsbAccessBySerialUseCase(systemManager)
        val res = getCase.invoke()
        assert(res is ApiResult.Success && res.data == "OFF")
    }

    @Test
    fun setUsbAccessBySerial_Off() = runTest {
        val serials = listOf("OFF")
        val useCase = SetUsbAccessBySerialUseCase(systemManager)
        val result = useCase.invoke(false, serials)
        assert(result is ApiResult.Success)
        val getCase = GetUsbAccessBySerialUseCase(systemManager)
        val res = getCase.invoke()
        assert(res is ApiResult.Success && res.data == "OFF")
    }

    @Test
    fun setUsbDeviceAccessAllowedList() = runTest {
        val vidpid = "1234:4321"
        val useCase = SetUsbDeviceAccessAllowedListUseCase(systemManager)
        val result = useCase.invoke(true, vidpid)
        assert(result is ApiResult.Success)
        val getCase = GetUsbDeviceAccessAllowedListUseCase(systemManager)
        val res = getCase.invoke()
        assert(res is ApiResult.Success && res.data == vidpid)
    }

    @Test
    fun usbDeviceAccessAllowedList_DisableAndClear() = runTest {
        val vidpid = "OFF"
        val useCase = SetUsbDeviceAccessAllowedListUseCase(systemManager)
        val result = useCase.invoke(false, vidpid)
        assert(result is ApiResult.Success)
        val getCase = GetUsbDeviceAccessAllowedListUseCase(systemManager)
        val res = getCase.invoke()
        assert(res is ApiResult.Success && res.data == vidpid)
    }

    @Test
    fun usbDeviceAccessAllowedList_Disable() = runTest {
        val vidpid = "1234:4321"
        val useCase = SetUsbDeviceAccessAllowedListUseCase(systemManager)
        val result = useCase.invoke(false, vidpid)
        assert(result is ApiResult.Success)
        val getCase = GetUsbDeviceAccessAllowedListUseCase(systemManager)
        val res = getCase.invoke()
        assert(res is ApiResult.Success && res.data == "OFF")
    }

    @After
    fun cleanup() = runTest {
        val vidpid = "1234:4321"
        val useCase = SetUsbDeviceAccessAllowedListUseCase(systemManager)
        val result = useCase.invoke(false, vidpid)
    }
}