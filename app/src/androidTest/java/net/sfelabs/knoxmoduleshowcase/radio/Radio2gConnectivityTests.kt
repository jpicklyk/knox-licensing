package net.sfelabs.knoxmoduleshowcase.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.radio.Is2gConnectivityEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Set2gConnectivityEnabled
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 131)
class Radio2gConnectivityTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private var radio2gAllowed by Delegates.notNull<Boolean>()

    @Before
    fun getCurrentState() = runTest {
        val result = Is2gConnectivityEnabledUseCase(systemManager).invoke()
        if(result is ApiCall.Success) {
            radio2gAllowed = result.data
        }
    }

    @Test
    fun set2G_disabled() = runTest {
        val result = Set2gConnectivityEnabled(systemManager).invoke(false)
        assert(result is ApiCall.Success)
        val result2 = Is2gConnectivityEnabledUseCase(systemManager).invoke()
        assert(result2 is ApiCall.Success && !result2.data)
    }

    @Test
    fun set2G_enabled() = runTest {
        val result = Set2gConnectivityEnabled(systemManager).invoke(true)
        assert(result is ApiCall.Success)
        val result2 = Is2gConnectivityEnabledUseCase(systemManager).invoke()
        assert(result2 is ApiCall.Success && result2.data)
    }

    @After
    fun cleanup() = runTest {
        Set2gConnectivityEnabled(systemManager).invoke(radio2gAllowed)
    }
}