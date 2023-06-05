package net.sfelabs.knoxmoduleshowcase.usb

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.UsbConnectionType
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbConnectionTypeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbConnectionTypeUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsbConnectionTypeTest {
    private val sm = KnoxModule.provideKnoxSystemManager()
/*
    @Test
    fun testDefaultMTP() = runTest {
        val getUseCase = GetUsbConnectionTypeUseCase(sm)
        val result = getUseCase.invoke()
        assert(result is ApiCall.Success && result.data is UsbConnectionType.MTP)
    }

    @Test
    fun testTethering() = runTest {
        val setUseCase = SetUsbConnectionTypeUseCase(sm)
        val apiResult = setUseCase.invoke(UsbConnectionType.Tethering)
        assert(apiResult is ApiCall.Success)
        Thread.sleep(1000)
        val getUseCase = GetUsbConnectionTypeUseCase(sm)
        val result = getUseCase.invoke()
        assert(result is ApiCall.Success && result.data is UsbConnectionType.Tethering)
    }

 */
    @Test
    fun testMTP() = runTest {
        val setUseCase = SetUsbConnectionTypeUseCase(sm)
        val apiResult = setUseCase.invoke(UsbConnectionType.MTP)
        assert(apiResult is ApiCall.Success)

        val getUseCase = GetUsbConnectionTypeUseCase(sm)
        val result = getUseCase.invoke()
        assert(result is ApiCall.Success && result.data is UsbConnectionType.MTP)
    }
}