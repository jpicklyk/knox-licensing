package net.sfelabs.knoxmoduleshowcase.manual_tests

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 110)
class AutoCallTests {

    private lateinit var context: Context
    private lateinit var systemManager: SystemManager

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        systemManager = CustomDeviceManager.getInstance().systemManager
    }


    @Test
    fun checkActivationInfo() = runTest{
        val result = systemManager.addAutoCallNumber("15192407948", 2, CustomDeviceManager.ANSWER_MODE_SPEAKER)
        assert(result == CustomDeviceManager.SUCCESS)

        val list = systemManager.autoCallNumberList
        assert(list.isNotEmpty())

        val res = systemManager.setAutoCallPickupState(CustomDeviceManager.ENABLE)
        assert(res == CustomDeviceManager.SUCCESS)
    }
}