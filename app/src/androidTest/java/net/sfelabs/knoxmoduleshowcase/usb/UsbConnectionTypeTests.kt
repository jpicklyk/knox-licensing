package net.sfelabs.knoxmoduleshowcase.usb

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.UsbConnectionType
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbConnectionTypeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbConnectionTypeUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class UsbConnectionTypeTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()

    @Test
    @FlakyTest
    fun testUsbConnectionType_RndisTethering() = runTest {
        val setUseCase = SetUsbConnectionTypeUseCase(systemManager)
        val apiResult = setUseCase.invoke(UsbConnectionType.Tethering)
        assert(apiResult is ApiCall.Success)
        val result = GetUsbConnectionTypeUseCase(systemManager).invoke()
        assert(result is ApiCall.Success && result.data == UsbConnectionType.Tethering)
    }

}