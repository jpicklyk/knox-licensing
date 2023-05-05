package net.sfelabs.knoxmoduleshowcase.usb

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule.provideKnoxSystemManager
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbDeviceAccessAllowedListUseCase
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class UsbDeviceAccessAllowedListTests {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val sm = provideKnoxSystemManager()

    @Test
    fun testSetUsbDeviceAccessAllowedList() = runTest {
        val useCase = SetUsbDeviceAccessAllowedListUseCase(sm)

        val result = useCase.invoke(true, "1234:4321")
        assert(result is ApiCall.Success)
    }
}