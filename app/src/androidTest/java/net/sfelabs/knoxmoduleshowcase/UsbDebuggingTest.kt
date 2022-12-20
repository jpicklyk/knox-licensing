package net.sfelabs.knoxmoduleshowcase

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsbDebuggingTest {
    private lateinit var context: Context


    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
    }

//    This test works but breaks debugging and makes it very difficult to recover.  Leaving the
//    test here but commented out for historical keeping.
//    @Test
//    fun disableUsbDebugging() = runTest {
//        val edm = KnoxModule.provideKnoxEnterpriseDeviceManager(context)
//        val useCase =
//            net.sfelabs.knox_tactical.domain.use_cases.tactical.adb.SetUsbDebuggingUseCase(edm)
//        val result = useCase.invoke(false)
//        assert(result is ApiCall.Success)
//    }
}