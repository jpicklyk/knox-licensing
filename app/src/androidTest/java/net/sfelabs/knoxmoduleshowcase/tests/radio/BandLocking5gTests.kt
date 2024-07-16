package net.sfelabs.knoxmoduleshowcase.tests.radio

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
        if (result is ApiResult.Success) {
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
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun disable5gBandLockingPerSimSlotId0_simRemoved_returnError() = runTest {
        val result = Disable5gBandLockingUseCase(systemManager).invoke(0)
        assertTrue(
            "disableBandLocking API should return error when there is no sim card present for simSlotId 0",
            result is ApiResult.Error
        )
    }

    @Test
    @SimRemoved
    fun enable5gBandLocking_simRemoved_returnError() = runTest {
        val result = Enable5gBandLockingUseCase(systemManager).invoke(78)
        assertTrue("enable5gBandLocking API should return an error", result is ApiResult.Error)
    }

    @Test
    @SimRemoved
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enable5gBandLockingPerSimSlotId0_simRemoved_returnError() = runTest {
        val result = Enable5gBandLockingUseCase(systemManager).invoke(78, 0)
        assertTrue("enable5gBandLocking API should return an error for simSlotId 0", result is ApiResult.Error)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enable5gBandLockingPerSimSlotId1_simRequiredSlot0_returnError() = runTest {
        val result = Enable5gBandLockingUseCase(systemManager).invoke(78, 1)
        assertTrue("enable5gBandLocking API should return an error for simSlotId 1 when there is no eSIM installed.", result is ApiResult.Error)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun disable5gBandLockingPerSimSlotId1_simRequiredSlot0_returnError() = runTest {
        val result = Disable5gBandLockingUseCase(systemManager).invoke(1)
        assertTrue("disable5gBandLocking API should return an error for simSlotId 1 when there is no eSIM installed.", result is ApiResult.Error)
    }

    @Test
    @SimRequired
    fun enable5gBandLocking_confirmN78_returnSuccess() = runTest {
        testEnableBandLocking(null)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enable5gBandLockingPerSimSlotId0_confirmN78_returnSuccess() = runTest {
        testEnableBandLocking(0)
    }

    private suspend fun testEnableBandLocking(simSlotId: Int?) {
        val band = 78
        val result = Enable5gBandLockingUseCase(systemManager).invoke(band, simSlotId)
        assertTrue("enable5gBandLocking API should return success.  Error: $result",result is ApiResult.Success)

        val result2 = Get5gBandLockingUseCase(systemManager).invoke(simSlotId)
        assert(result2 is ApiResult.Success && result2.data.enabled && result2.data.value == band)
    }

    @Test
    @SimRequired
    fun enable5gBandLocking_BANDLOCK_NONE_notAllowed() = runTest {
        testBandLockNoneNotAllowed(null)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enable5gBandLockingPerSimSlotId0_BANDLOCK_NONE_notAllowed() = runTest {
        testBandLockNoneNotAllowed(0)
    }

    private suspend fun testBandLockNoneNotAllowed(simSlotId: Int?) {
        val band = -1
        val result = Enable5gBandLockingUseCase(systemManager).invoke(band, simSlotId)
        assertTrue("enable5gBandLocking API should return an error", result is ApiResult.Error)

        val result2 = Get5gBandLockingUseCase(systemManager).invoke(simSlotId)
        assert(result2 is ApiResult.Success)
        assertTrue("Band locking should be disabled", !(result2 as ApiResult.Success).data.enabled)
        assertTrue("Band locking state should report BAND_LOCKING_NONE", result2.data.value == band)
    }

    @Test
    fun enable5gBandLocking_invalidParameter_returnError() = runTest {
        testInvalidParameter(null)
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enable5gBandLockingPerSimSlotId0_invalidParameter_returnError() = runTest {
        testInvalidParameter(0)
    }

    private suspend fun testInvalidParameter(simSlotId: Int?) {
        val band = -2
        val result = Enable5gBandLockingUseCase(systemManager).invoke(band, simSlotId)
        assert(result is ApiResult.Error)
    }

    @Test
    @SimRequired
    fun disable5gBandLocking_returnSuccess() = runTest {
        testDisableBandLocking(null)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun disable5gBandLockingPerSimSlotId0_returnSuccess() = runTest {
        testDisableBandLocking(0)
    }

    private suspend fun testDisableBandLocking(simSlotId: Int?) {
        val result = Disable5gBandLockingUseCase(systemManager).invoke(simSlotId)
        assertTrue("disable5GBandLocking API should return success.  Error: $result",result is ApiResult.Success)

        val result2 = Get5gBandLockingUseCase(systemManager).invoke(simSlotId)
        assert(result2 is ApiResult.Success && !result2.data.enabled)
    }

    @After
    fun cleanup() = runTest {
        Disable5gBandLockingUseCase(systemManager).invoke()
    }
}