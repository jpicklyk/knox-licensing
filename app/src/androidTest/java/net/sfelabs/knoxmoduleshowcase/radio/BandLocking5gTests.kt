package net.sfelabs.knoxmoduleshowcase.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.radio.Disable5gBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Enable5gBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Get5gBandLockingUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 131)
class BandLocking5gTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private var currentBand by Delegates.notNull<Int>()

    @Before
    fun recordCurrentBandLocking() = runTest {
        val result = Get5gBandLockingUseCase(systemManager).invoke()
        if(result is ApiResult.Success) {
            currentBand = result.data.value
        }
    }

    @Test
    fun enable5gBandLockingN78() = runTest {
        val band = 78
        val result = Enable5gBandLockingUseCase(systemManager).invoke(band)
        assert(result is ApiResult.Success)

        val result2 = Get5gBandLockingUseCase(systemManager).invoke()
        assert(result2 is ApiResult.Success && result2.data.enabled && result2.data.value == band)
    }

    /**
     * If -1 (BANDLOCK_NONE) is passed to the enable5gBandLocking API, it should be successful
     * but band locking should continue to report as disabled.
     */
    @Test
    fun enable5gBandLocking_BANDLOCK_NONE() = runTest {
        val band = -1
        val result = Enable5gBandLockingUseCase(systemManager).invoke(band)
        assert(result is ApiResult.Success)

        val result2 = Get5gBandLockingUseCase(systemManager).invoke()
        assert(result2 is ApiResult.Success && !result2.data.enabled && result2.data.value == band)
    }

    @Test
    fun enable5gBandLocking_invalidParameter() = runTest {
        val band = -2
        val result = Enable5gBandLockingUseCase(systemManager).invoke(band)
        assert(result is ApiResult.Error)
    }

    @Test
    fun disable5gBandLocking() = runTest {
        val result = Disable5gBandLockingUseCase(systemManager).invoke()
        assert(result is ApiResult.Success)

        val result2 = Get5gBandLockingUseCase(systemManager).invoke()
        assert(result2 is ApiResult.Success && !result2.data.enabled)
    }

    @After
    fun cleanup() = runTest {
        Disable5gBandLockingUseCase(systemManager).invoke()
    }
}