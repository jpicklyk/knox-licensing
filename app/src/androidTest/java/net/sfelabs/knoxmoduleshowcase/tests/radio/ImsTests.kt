package net.sfelabs.knoxmoduleshowcase.tests.radio

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.knox.android.AndroidApplicationContextProvider
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.ims.IsImsEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ims.SetImsEnabled
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 131)
class ImsTests {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private var imsEnabled by Delegates.notNull<Boolean>()

    @Before
    fun readCurrentState() = runTest {
        val testProvider = object: AndroidApplicationContextProvider {
            override fun getContext(): Context {
                return context
            }
        }
        AndroidApplicationContextProvider.init(testProvider)

        val result = IsImsEnabledUseCase().invoke(0)
        if(result is ApiResult.Success) {
            imsEnabled = result.data
        }
    }

    @Test
    fun setImsEnabled() = runTest {
        val result = SetImsEnabled().invoke(enable = true)
        assert(result is ApiResult.Success)
        val result2 = IsImsEnabledUseCase().invoke(0)
        assert(result2 is ApiResult.Success && result2.data)
    }

    @Test
    fun setImsDisabled() = runTest {
        val result = SetImsEnabled().invoke(enable = false)
        assert(result is ApiResult.Success)
        val result2 = IsImsEnabledUseCase().invoke(0)
        assert(result2 is ApiResult.Success && !result2.data)
    }

    @Test
    fun setInvalidImsFeature() = runTest {
        val result = SetImsEnabled().invoke(0, 0, false)
        assert(result is ApiResult.Error)
    }

    @Test
    fun setInvalidSimSlotId() = runTest {
        val result = SetImsEnabled().invoke(8, false)
        assert(result is ApiResult.Error)
    }

    @After
    fun resetImsState() = runTest {
        SetImsEnabled().invoke(enable = imsEnabled)
    }
}