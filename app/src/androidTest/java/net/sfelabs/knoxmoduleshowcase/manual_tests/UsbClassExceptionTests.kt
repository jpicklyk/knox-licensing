package net.sfelabs.knoxmoduleshowcase.manual_tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.restriction.RestrictionPolicy.USBInterface
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_common.di.KnoxModule
import net.sfelabs.knox_common.domain.use_cases.SetUsbExceptionListUseCase
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Manual tests required for each of the below Test functions.
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
class UsbClassExceptionTests {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val restrictionPolicy = KnoxModule.provideKnoxRestrictionPolicy(appContext)

    /**
     * After running this function, verify if using a USB ethernet dongle can successfully be used
     * to connect to ethernet.
     */
    @Test
    fun setCdcOnly() = runTest {
        val usbClasses: Int = USBInterface.CDC.value
        val useCase = SetUsbExceptionListUseCase(restrictionPolicy).invoke(usbClasses)
        assert(useCase is ApiResult.Success)
    }

    /**
     * After running this test function, verify if using a USB HUB that both an ethernet dongle and
     * USB connected thumb drive can successfully be used.
     */
    @Test
    fun setCdcAndMas() = runTest {
        val usbClasses: Int = USBInterface.CDC.value or USBInterface.MAS.value
        val useCase = SetUsbExceptionListUseCase(restrictionPolicy).invoke(usbClasses)
        assert(useCase is ApiResult.Success)
    }

    @Test
    fun setMasOnly() = runTest {
        val usbClasses: Int = USBInterface.MAS.value
        val useCase = SetUsbExceptionListUseCase(restrictionPolicy).invoke(usbClasses)
        assert(useCase is ApiResult.Success)
    }


    @Test
    fun setAllOpen() = runTest {
        val usbClasses: Int = USBInterface.OFF.value
        val useCase = SetUsbExceptionListUseCase(restrictionPolicy).invoke(usbClasses)
        assert(useCase is ApiResult.Success)
    }
}