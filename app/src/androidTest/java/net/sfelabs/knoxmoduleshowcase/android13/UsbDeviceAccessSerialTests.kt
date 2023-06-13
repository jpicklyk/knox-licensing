package net.sfelabs.knoxmoduleshowcase.android13

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbAccessBySerialUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbAccessBySerialUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsbDeviceAccessSerialTests {
    private val sm = KnoxModule.provideKnoxSystemManager()

    @Test
    fun setUsbAccessBySerial_SingleSerialNumber() = runTest {
        val serials = listOf("0504a2635")
        val useCase = SetUsbAccessBySerialUseCase(sm)
        val result = useCase.invoke(true, serials)
        assert(result is ApiCall.Success)
        val getCase = GetUsbAccessBySerialUseCase(sm)
        val res = getCase.invoke()
        assert(res is ApiCall.Success && res.data == "0504a2635")
    }

    @Test
    fun setUsbAccessBySerial_MultipleSerialNumber() = runTest {
        val serials = listOf("123456789abc", "cba987654321")
        val useCase = SetUsbAccessBySerialUseCase(sm)
        val result = useCase.invoke(true, serials)
        assert(result is ApiCall.Success)
        val getCase = GetUsbAccessBySerialUseCase(sm)
        val res = getCase.invoke()
        assert(res is ApiCall.Success && res.data == "123456789abc:cba987654321")
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

    @Test
    fun setUsbAccessBySerial_Off() = runTest {
        val serials = listOf("OFF")
        val useCase = SetUsbAccessBySerialUseCase(sm)
        val result = useCase.invoke(false, serials)
        assert(result is ApiCall.Success)
        val getCase = GetUsbAccessBySerialUseCase(sm)
        val res = getCase.invoke()
        assert(res is ApiCall.Success && res.data == "OFF")
    }
}