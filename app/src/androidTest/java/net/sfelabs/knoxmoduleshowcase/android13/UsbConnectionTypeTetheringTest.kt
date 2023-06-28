package net.sfelabs.knoxmoduleshowcase.android13

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.UsbConnectionType
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbConnectionTypeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbConnectionTypeUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class UsbConnectionTypeTetheringTest {
    private val sm = KnoxModule.provideKnoxSystemManager()

    @Test
    @FlakyTest
    fun testUsbConnectionType_RndisTethering() = runTest {
        val setUseCase = SetUsbConnectionTypeUseCase(sm)
        val apiResult = setUseCase.invoke(UsbConnectionType.Tethering)
        assert(apiResult is ApiCall.Success)
        val result = GetUsbConnectionTypeUseCase(sm).invoke()
        assert(result is ApiCall.Success && result.data == UsbConnectionType.Tethering)
    }

}