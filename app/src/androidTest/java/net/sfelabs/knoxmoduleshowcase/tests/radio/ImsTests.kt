package net.sfelabs.knoxmoduleshowcase.tests.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox.core.testing.context.AndroidContextProviderRule
import net.sfelabs.knox.core.testing.rules.SimRequired
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.ims.IsImsEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ims.SetImsEnabled
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 131)
class ImsTests {
    @get:Rule
    val contextRule = AndroidContextProviderRule()

    private var imsEnabled by Delegates.notNull<Boolean>()

    @Before
    fun readCurrentState() = runTest {
        val result = IsImsEnabledUseCase().invoke(0)
        if (result is ApiResult.Success) {
            imsEnabled = result.data.enabled
        }
    }

    @Test
    @SimRequired
    fun setImsEnabledSlotId0() = runTest {
        val result = SetImsEnabled().invoke(enable = true)
        assertTrue("Set IMS enabled failed: ${result.getErrorOrNull()}", result is ApiResult.Success)
        val result2 = IsImsEnabledUseCase().invoke(0)
        assert(result2 is ApiResult.Success && result2.data.enabled)
    }

    @Test
    @SimRequired
    fun setImsDisabledSlotId0() = runTest {
        val result = SetImsEnabled().invoke(enable = false)
        assertTrue("Set IMS disabled failed: ${result.getErrorOrNull()}", result is ApiResult.Success)
        val result2 = IsImsEnabledUseCase().invoke(0)
        assert(result2 is ApiResult.Success && !result2.data.enabled)
    }

    @Test
    @SimRequired
    fun setInvalidImsFeature() = runTest {
        val result = SetImsEnabled().invoke(0, 0, false)
        assert(result is ApiResult.Error)
    }

    @Test
    @SimRequired
    fun setInvalidSimSlotId() = runTest {
        val result = SetImsEnabled().invoke(8, false)
        assert(result is ApiResult.Error)
    }

    @After
    fun resetImsState() = runTest {
        SetImsEnabled().invoke(enable = imsEnabled)
    }
}