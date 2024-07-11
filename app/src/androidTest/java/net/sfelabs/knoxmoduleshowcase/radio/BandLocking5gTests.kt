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
import net.sfelabs.knox_tactical.domain.use_cases.radio.Disable5gBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Enable5gBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Get5gBandLockingUseCase
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
class BandLocking5gTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private var currentBand by Delegates.notNull<Int>()

    @get:Rule
    val simRequiredRule = SimRequiredRule()
    @get:Rule
    val simMustBeRemovedRule = SimMustBeRemovedRule()

    @Before
    fun recordCurrentBandLocking() = runTest {
        val result = Get5gBandLockingUseCase(systemManager).invoke()
        if(result is ApiResult.Success) {
            currentBand = result.data.value
        }
    }

    @Test
    @SimRemoved
    fun disable5gBandLocking_simRemoved_returnError() = runTest {
        val result = Disable5gBandLockingUseCase(systemManager).invoke()
        assertTrue(
            "disableBandLocking API should return error when there is no sim card present",
            result is ApiResult.Error
        )
    }
    @Test
    @SimRemoved
    fun enable5gBandLocking_simRemoved_returnError() = runTest {
        val band = 78
        val result = Enable5gBandLockingUseCase(systemManager).invoke(band)
        assertTrue("enable5gBandLocking API should return an error",result is ApiResult.Error)
    }

    @Test
    @SimRequired
    fun enable5gBandLocking_confirmN78_returnSuccess() = runTest {
        val band = 78
        val result = Enable5gBandLockingUseCase(systemManager).invoke(band)
        assert(result is ApiResult.Success)

        val result2 = Get5gBandLockingUseCase(systemManager).invoke()
        assert(result2 is ApiResult.Success && result2.data.enabled && result2.data.value == band)
    }


    @Test
    @SimRequired
    fun enable5gBandLocking_BANDLOCK_NONE_notAllowed() = runTest {
        val band = -1
        val result = Enable5gBandLockingUseCase(systemManager).invoke(band)
        assertTrue("enable5gBandLocking API should return an error",result is ApiResult.Error)

        val result2 = Get5gBandLockingUseCase(systemManager).invoke()
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
    fun enable5gBandLocking_invalidParameter_returnError() = runTest {
        val band = -2
        val result = Enable5gBandLockingUseCase(systemManager).invoke(band)
        assert(result is ApiResult.Error)
    }

    @Test
    @SimRequired
    fun disable5gBandLocking_returnSuccess() = runTest {
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