package net.sfelabs.knoxmoduleshowcase.android10

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.UsbConnectionType
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbConnectionTypeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbConnectionTypeUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsbConnectionTypeMtpTest {
    private val sm = KnoxModule.provideKnoxSystemManager()

    @Test
    fun testMTP() = runTest {
        val setUseCase = SetUsbConnectionTypeUseCase(sm)
        val apiResult = setUseCase.invoke(UsbConnectionType.MTP)
        assert(apiResult is ApiCall.Success)

        val getUseCase = GetUsbConnectionTypeUseCase(sm)
        val result = getUseCase.invoke()
        assert(result is ApiCall.Success && result.data == UsbConnectionType.MTP)
    }

}