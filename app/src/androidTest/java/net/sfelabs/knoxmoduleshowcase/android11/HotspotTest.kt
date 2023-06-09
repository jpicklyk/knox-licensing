package net.sfelabs.knoxmoduleshowcase.android11

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.hotspot2.PasspointConfiguration
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.di.AndroidServiceModule
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.hotspot.GetHotspot20StateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hotspot.SetHotspot20StateUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HotspotTest {
    private lateinit var context: Context
    private val sm = KnoxModule.provideKnoxSettingsManager()
    private lateinit var wifiManager: WifiManager

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
        wifiManager = AndroidServiceModule.provideWifiManager(context)
    }

    @Test
    fun testHotspot20Return() = runTest {
        val useCase = GetHotspot20StateUseCase(sm, wifiManager)

        val result = useCase.invoke()
        assert(result is ApiCall.Success)
    }

    @Test
    fun testEnableHotspot20() = runTest {
        val setUseCase = SetHotspot20StateUseCase(sm)
        val getUseCase = GetHotspot20StateUseCase(sm, wifiManager)

        val result = setUseCase.invoke(true)
        assert(result is ApiCall.Success)
        val result2 = getUseCase.invoke()
        assert(result2 is ApiCall.Success && result2.data)
    }

    @Test
    fun testDisableHotspot20() = runTest {
        val setUseCase = SetHotspot20StateUseCase(sm)
        val getUseCase = GetHotspot20StateUseCase(sm, wifiManager)

        val result = setUseCase.invoke(false)
        assert(result is ApiCall.Success)

        val result2 = getUseCase.invoke()
        assert(result2 is ApiCall.Success && !result2.data)
    }

    @After
    fun disableHotspot() = runTest {
        SetHotspot20StateUseCase(sm).invoke(false)
    }
}