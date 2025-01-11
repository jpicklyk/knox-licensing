package net.sfelabs.knoxmoduleshowcase.manual_tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.UsbConnectionType
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetUsbConnectionTypeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbConnectionTypeUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsbConnectionTypeTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private lateinit var currentType: UsbConnectionType
    @Before
    fun setup() = runTest {
        val result = GetUsbConnectionTypeUseCase(systemManager).invoke()
        if(result is ApiResult.Success) {
            currentType = result.data
        }
    }

    /**
     * Testing MTP seems to change the wireless debugging port so it breaks testing.
     * Going to comment this out as it isn't really needed.

    @Test
    fun testMTP() = runTest {
        val setUseCase = SetUsbConnectionTypeUseCase(systemManager)
        val apiResult = setUseCase.invoke(UsbConnectionType.MTP)
        assert(apiResult is ApiCall.Success)
        Thread.sleep(1000)
        val getUseCase = GetUsbConnectionTypeUseCase(systemManager)
        val result = getUseCase.invoke()
        assert(result is ApiCall.Success && result.data == UsbConnectionType.MTP)
    }
     */

    @Test
    @FlakyTest
    fun testUsbConnectionType_RndisTethering() = runTest {
        val setUseCase = SetUsbConnectionTypeUseCase(systemManager)
        val apiResult = setUseCase.invoke(UsbConnectionType.Tethering)
        assert(apiResult is ApiResult.Success)
        Thread.sleep(1000)
        val result = GetUsbConnectionTypeUseCase(systemManager).invoke()
        assert(result is ApiResult.Success && result.data == UsbConnectionType.Tethering)
    }

    @After
    fun resetConnectionType() = runTest {
        SetUsbConnectionTypeUseCase(systemManager).invoke(currentType)
    }

}