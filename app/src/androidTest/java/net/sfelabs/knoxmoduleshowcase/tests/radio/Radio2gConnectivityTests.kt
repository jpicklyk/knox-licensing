package net.sfelabs.knoxmoduleshowcase.tests.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.radio.Is2gConnectivityEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Set2gConnectivityEnabled
import net.sfelabs.knoxmoduleshowcase.app.decodeAllowedNetworkTypes
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

/**
 * Can validate that GSM/CDMA is all disabled via  hidden app service mode.
 * *#2263*
 * Click SIM and then GSM band preference
 */
@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 131)
class Radio2gConnectivityTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private var radio2gAllowed by Delegates.notNull<Boolean>()
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun getCurrentState() = runTest {
        val result = Is2gConnectivityEnabledUseCase(systemManager).invoke()
        if(result is ApiResult.Success) {
            radio2gAllowed = result.data
        }
    }

    @Test
    fun set2G_disabled() = runTest {
        val result = Set2gConnectivityEnabled(systemManager).invoke(false)
        assert(result is ApiResult.Success)
        val result2 = Is2gConnectivityEnabledUseCase(systemManager).invoke()
        assert(result2 is ApiResult.Success && !result2.data )
        //println("2G available? ${is2GSupported()}")

        //Lets check another way to ensure it
    }

    @Test
    fun set2G_enabled() = runTest {
        val result = Set2gConnectivityEnabled(systemManager).invoke(true)
        assert(result is ApiResult.Success)
        val result2 = Is2gConnectivityEnabledUseCase(systemManager).invoke()
        assert(result2 is ApiResult.Success && result2.data)
    }

    @After
    fun cleanup() = runTest {
        Set2gConnectivityEnabled(systemManager).invoke(radio2gAllowed)
    }

    // Method to check if 2G (GSM) connectivity is available
    private fun is2GSupported(): Boolean {
        val allowedNetworkTypeValue: Long = 578511
        val types = decodeAllowedNetworkTypes(allowedNetworkTypeValue)

        // Check if the telephony manager is not null and if 2G (GSM) is among the supported network types
        return types.contains("GSM") || types.contains("GPRS") || types.contains("EDGE") ||
                types.contains("CDMA")
    }

    //Manual test...

    fun printAllowedNetworkTypes() {
        //OUTPUT FROM: adb shell dumpsys telephony.registry | findstr "mAllowedNetworkTypeValue"
        val value: Long = 578511
        val types = decodeAllowedNetworkTypes(value)
        println("Allowed network types: $types")
    }

}