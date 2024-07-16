package net.sfelabs.knoxmoduleshowcase.tests.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.tdm.GetTacticalDeviceModeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tdm.SetTacticalDeviceModeUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 100)
class TacticalDeviceModeTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val restrictionPolicy = KnoxModule.provideKnoxRestrictionPolicy(
        EnterpriseDeviceManager.getInstance(context)
    )
    private var currentlyEnabled by Delegates.notNull<Boolean>()

    @Before
    fun setup() = runTest {
        val result = GetTacticalDeviceModeUseCase(restrictionPolicy).invoke()
        if(result is ApiResult.Success) {
            currentlyEnabled = result.data.enabled
        }
    }
    @Test
    fun enableTacticalDeviceMode() = runTest {
        val res1 = SetTacticalDeviceModeUseCase(restrictionPolicy).invoke(true)
        assert(res1 is ApiResult.Success)
        val res2 = GetTacticalDeviceModeUseCase(restrictionPolicy).invoke()
        assert(res2 is ApiResult.Success && res2.data.enabled)
    }

    @Test
    fun disableTacticalDeviceMode() = runTest {
        val res1 = SetTacticalDeviceModeUseCase(restrictionPolicy).invoke(false)
        assert(res1 is ApiResult.Success)
        val res2 = GetTacticalDeviceModeUseCase(restrictionPolicy).invoke()
        assert(res2 is ApiResult.Success && !res2.data.enabled)
    }

    @After
    fun cleanup() = runTest {
        SetTacticalDeviceModeUseCase(restrictionPolicy).invoke(currentlyEnabled)
    }
}