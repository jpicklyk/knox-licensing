package net.sfelabs.knoxmoduleshowcase.tests.wifi

import android.content.Context
import android.net.wifi.WifiManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.di.AndroidServiceModule
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.hotspot.IsHotspot20EnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hotspot.SetHotspot20EnabledUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 110)
class HotspotTest {
    private lateinit var context: Context
    private lateinit var wifiManager: WifiManager

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
        wifiManager = AndroidServiceModule.provideWifiManager(context)
    }

    @Test
    fun testHotspot20Return() = runTest {
        val useCase = IsHotspot20EnabledUseCase()

        val result = useCase.invoke()
        assert(result is ApiResult.Success)
    }

    @Test
    fun testEnableHotspot20() = runTest {
        val setUseCase = SetHotspot20EnabledUseCase()
        val getUseCase = IsHotspot20EnabledUseCase()

        val result = setUseCase.invoke(true)
        assert(result is ApiResult.Success)
        val result2 = getUseCase.invoke()
        assert(result2 is ApiResult.Success && result2.data)
    }

    @Test
    fun testDisableHotspot20() = runTest {
        val setUseCase = SetHotspot20EnabledUseCase()
        val getUseCase = IsHotspot20EnabledUseCase()

        val result = setUseCase.invoke(false)
        assert(result is ApiResult.Success)

        val result2 = getUseCase.invoke()
        assert(result2 is ApiResult.Success && !result2.data)
    }

    @After
    fun disableHotspot() = runTest {
        SetHotspot20EnabledUseCase().invoke(false)
    }
}