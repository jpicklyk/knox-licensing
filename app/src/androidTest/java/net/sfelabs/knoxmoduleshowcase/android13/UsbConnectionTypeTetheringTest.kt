package net.sfelabs.knoxmoduleshowcase.android13

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.UsbConnectionType
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbConnectionTypeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbConnectionTypeUseCase
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsbConnectionTypeTetheringTest {
    private val sm = KnoxModule.provideKnoxSystemManager()

    @Test
    fun testUsbConnectionType_RndisTethering() = runTest {
        val setUseCase = SetUsbConnectionTypeUseCase(sm)
        val apiResult = setUseCase.invoke(UsbConnectionType.Tethering)
        assert(apiResult is ApiCall.Success)
        Thread.sleep(1000)
        val getUseCase = GetUsbConnectionTypeUseCase(sm)
        val result = getUseCase.invoke()
        assert(result is ApiCall.Success && result.data is UsbConnectionType.Tethering)
    }

    @After
    fun resetToDefaultConnectionType() = runTest {
        SetUsbConnectionTypeUseCase(sm).invoke(UsbConnectionType.Default)
    }
}