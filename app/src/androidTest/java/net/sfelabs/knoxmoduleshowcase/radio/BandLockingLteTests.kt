package net.sfelabs.knoxmoduleshowcase.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.testing.rules.SimMustBeRemovedRule
import net.sfelabs.core.testing.rules.SimRemoved
import net.sfelabs.core.testing.rules.SimRequired
import net.sfelabs.core.testing.rules.SimRequiredRule
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.radio.DisableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.EnableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.GetBandLockingStateUseCase
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 100)
class BandLockingLteTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private var currentBand by Delegates.notNull<Int>()

    @get:Rule
    val simRequiredRule = SimRequiredRule()
    @get:Rule
    val simMustBeRemovedRule = SimMustBeRemovedRule()

    @Before
    fun recordCurrentBandLocking() = runTest {
        val result = GetBandLockingStateUseCase(systemManager).invoke()
        if(result is ApiResult.Success) {
            currentBand = result.data.value
        }
    }

    @Test
    @SimRemoved
    fun disableBandLocking_simRemoved_returnError() = runTest {
        val result = DisableBandLockingUseCase(systemManager).invoke()
        assertTrue(
            "disableBandLocking API should return an error when there is no sim card present",
            result is ApiResult.Error
        )
    }
    @Test
    @SimRemoved
    fun enableBandLocking_simRemoved_returnError() = runTest {
        val band = 78
        val result = EnableBandLockingUseCase(systemManager).invoke(band)
        assertTrue("enableBandLocking API should return an error",result is ApiResult.Error)
    }

    @Test
    @SimRequired
    fun enableLteBandLocking_confirmBand8_returnSuccess() = runTest {
        val band = 8
        val result = EnableBandLockingUseCase(systemManager).invoke(band)
        assert(result is ApiResult.Success)

        val result2 = GetBandLockingStateUseCase(systemManager).invoke()
        assert(result2 is ApiResult.Success && result2.data.enabled && result2.data.value == band)
    }

    @Test
    @SimRequired
    fun enableLteBandLocking_BANDLOCK_NONE_notAllowed() = runTest {
        val band = -1
        val result = EnableBandLockingUseCase(systemManager).invoke(band)
        assertTrue("enableBandLocking API should return an error",result is ApiResult.Error)

        val result2 = GetBandLockingStateUseCase(systemManager).invoke()
        assert(result2 is ApiResult.Success)
        assertTrue(
            "Band locking should be disabled",
            !(result2 as ApiResult.Success).data.enabled
        )
        assertTrue(
            "Band locking state should report BAND_LOCKING_NONE",
            result2.data.value == band
        )
    }

    @Test
    fun enableLteBandLocking_invalidParameter_returnError() = runTest {
        val band = -2
        val result = EnableBandLockingUseCase(systemManager).invoke(band)
        assert(result is ApiResult.Error)
    }

    @Test
    @SimRequired
    fun disableLteBandLocking_returnSuccess() = runTest {
        val result = DisableBandLockingUseCase(systemManager).invoke()
        assert(result is ApiResult.Success)

        val result2 = GetBandLockingStateUseCase(systemManager).invoke()
        assert(result2 is ApiResult.Success && !result2.data.enabled)
    }

    @After
    fun cleanup() = runTest {
        if(currentBand == -1) {
            DisableBandLockingUseCase(systemManager).invoke()
        } else {
            EnableBandLockingUseCase(systemManager).invoke(currentBand)
        }
    }
}