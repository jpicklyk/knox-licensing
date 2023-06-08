package net.sfelabs.knoxmoduleshowcase.usb

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.checkMethodExistence
import net.sfelabs.knox_tactical.di.KnoxModule.provideKnoxSystemManager
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbAccessBySerialUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbDeviceAccessAllowedListUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbAccessBySerialUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbDeviceAccessAllowedListUseCase
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UsbDeviceAccessAllowedListTests {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val sm = provideKnoxSystemManager()


    @Test
    fun getUsbDeviceAccessAllowedList_Exists() = runTest {
        assert(checkMethodExistence(sm::class,"getUsbDeviceAccessAllowedList"))
    }

    @Test
    fun setUsbDeviceAccessAllowedList_Exists() = runTest {
        assert(checkMethodExistence(sm::class,"setUsbDeviceAccessAllowedList"))
    }

    @Test
    fun setUsbDeviceAccessAllowedListSerialNumber_Exists() = runTest {
        assert(checkMethodExistence(sm::class,"setUsbDeviceAccessAllowedListSerialNumber"))
    }

    @Test
    fun getUsbDeviceAccessAllowedListSerialNumber_Exists() = runTest {
        assert(checkMethodExistence(sm::class,"getUsbDeviceAccessAllowedListSerialNumber"))
    }


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

    @Test
    fun setUsbAccessBySerial_SingleSerialNumber() = runTest {
        val serials = listOf("0504a2635")
        val useCase = SetUsbAccessBySerialUseCase(sm)
        val result = useCase.invoke(true, serials)
        assert(result is ApiCall.Success)
        val getCase = GetUsbAccessBySerialUseCase(sm)
        val res = getCase.invoke()
        assert(res is ApiCall.Success && res.data == serials.toString())
    }

    @Test
    fun setUsbAccessBySerial_MultipleSerialNumber() = runTest {
        val serials = listOf("123456789abc", "cba987654321")
        val useCase = SetUsbAccessBySerialUseCase(sm)
        val result = useCase.invoke(true, serials)
        assert(result is ApiCall.Success)
        val getCase = GetUsbAccessBySerialUseCase(sm)
        val res = getCase.invoke()
        assert(res is ApiCall.Success && res.data == serials.toString())
    }

    @Test
    fun setUsbAccessBySerial_Disable() = runTest {
        val serials = listOf("123456789abc", "cba987654321")
        val useCase = SetUsbAccessBySerialUseCase(sm)
        val result = useCase.invoke(false, serials)
        assert(result is ApiCall.Success)
        val getCase = GetUsbAccessBySerialUseCase(sm)
        val res = getCase.invoke()
        assert(res is ApiCall.Success && res.data == "OFF")
    }
}