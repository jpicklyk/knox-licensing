package net.sfelabs.knoxmoduleshowcase.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.radio.DisableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.EnableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.GetBandLockingStateUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 100)
class BandLockingLteTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private var currentBand by Delegates.notNull<Int>()

    @Before
    fun recordCurrentBandLocking() = runTest {
        val result = GetBandLockingStateUseCase(systemManager).invoke()
        if(result is ApiCall.Success) {
            currentBand = result.data.apiValue
        }
    }

    @Test
    fun enableLteBandLocking_band8() = runTest {
        val band = 8
        val result = EnableBandLockingUseCase(systemManager).invoke(band)
        assert(result is ApiCall.Success)

        val result2 = GetBandLockingStateUseCase(systemManager).invoke()
        assert(result2 is ApiCall.Success && result2.data.enabled && result2.data.apiValue == band)
    }

    /**
     * If -1 (BANDLOCK_NONE) is passed to the enable5gBandLocking API, it should be successful
     * but band locking should continue to report as disabled.
     */
    @Test
    fun enableLteBandLocking_BANDLOCK_NONE() = runTest {
        val band = -1
        val result = EnableBandLockingUseCase(systemManager).invoke(band)
        assert(result is ApiCall.Success)

        val result2 = GetBandLockingStateUseCase(systemManager).invoke()
        assert(result2 is ApiCall.Success && !result2.data.enabled && result2.data.apiValue == band)
    }

    @Test
    fun enableLteBandLocking_invalidParameter() = runTest {
        val band = -2
        val result = EnableBandLockingUseCase(systemManager).invoke(band)
        assert(result is ApiCall.Error)
    }

    @Test
    fun disableLteBandLocking() = runTest {
        val result = DisableBandLockingUseCase(systemManager).invoke()
        assert(result is ApiCall.Success)

        val result2 = GetBandLockingStateUseCase(systemManager).invoke()
        assert(result2 is ApiCall.Success && !result2.data.enabled)
    }

    @After
    fun cleanup() = runTest {
        //BANDLOCK_NONE = -1
        if(currentBand == -1) {
            DisableBandLockingUseCase(systemManager).invoke()
        } else {
            EnableBandLockingUseCase(systemManager).invoke(currentBand)
        }
    }
}