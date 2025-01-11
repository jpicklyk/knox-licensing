package net.sfelabs.knoxmoduleshowcase.tests.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.knox.api.domain.model.ApiResult
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
        if (result is ApiResult.Success) {
            currentBand = result.data.value
        }
    }

    @Test
    @SimRemoved
    fun disableBandLocking_simRemoved_returnError() = runTest {
        testDisableBandLockingSimRemoved(null)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enableBandLockingPerSimSlotId1_simRequiredSlot0_returnError() = runTest {
        val result = EnableBandLockingUseCase(systemManager).invoke(78, 1)
        assertTrue("enableBandLocking API should return an error for simSlotId 1 when there is no eSIM installed.", result is ApiResult.Error)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun disableBandLockingPerSimSlotId1_simRequiredSlot0_returnError() = runTest {
        val result = DisableBandLockingUseCase(systemManager).invoke(1)
        assertTrue("disableBandLocking API should return an error for simSlotId 1 when there is no eSIM installed.", result is ApiResult.Error)
    }

    @Test
    @SimRemoved
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun disableBandLockingPerSimSlotId0_simRemoved_returnError() = runTest {
        testDisableBandLockingSimRemoved(0)
    }

    private suspend fun testDisableBandLockingSimRemoved(simSlotId: Int?) {
        val result = DisableBandLockingUseCase(systemManager).invoke(simSlotId)
        assertTrue(
            "disableBandLocking API should return an error when there is no sim card present",
            result is ApiResult.Error
        )
    }

    @Test
    @SimRemoved
    fun enableBandLocking_simRemoved_returnError() = runTest {
        testEnableBandLockingSimRemoved(null)
    }

    @Test
    @SimRemoved
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enableBandLockingPerSimSlotId0_simRemoved_returnError() = runTest {
        testEnableBandLockingSimRemoved(0)
    }

    private suspend fun testEnableBandLockingSimRemoved(simSlotId: Int?) {
        val band = 78
        val result = EnableBandLockingUseCase(systemManager).invoke(band, simSlotId)
        assertTrue("enableBandLocking API should return an error", result is ApiResult.Error)
    }

    @Test
    @SimRequired
    fun enableLteBandLocking_confirmBand8_returnSuccess() = runTest {
        testEnableLteBandLocking(null)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enableLteBandLockingPerSimSlotId0_confirmBand8_returnSuccess() = runTest {
        testEnableLteBandLocking(0)
        //confirm that SIM id 1 isn't also enabled
        val result = GetBandLockingStateUseCase(systemManager).invoke(1)
        assert(result is ApiResult.Success)
        val state = result.getOrNull()
        assertTrue("Band locking on SIM id 1 should not be enabled when configuring SIM 0.", state?.enabled == false)

        //confirm SIM id 1 does not have the band applied
        assertTrue("SIM id 1 should be returning -1 not ${state?.value}.", state?.value == -1)
    }

    private suspend fun testEnableLteBandLocking(simSlotId: Int?) {
        val band = 8
        val result = EnableBandLockingUseCase(systemManager).invoke(band, simSlotId)
        assert(result is ApiResult.Success)

        val result2 = GetBandLockingStateUseCase(systemManager).invoke(simSlotId)
        assert(result2 is ApiResult.Success && result2.data.enabled && result2.data.value == band)
    }

    @Test
    @SimRequired
    fun enableLteBandLocking_BANDLOCK_NONE_notAllowed() = runTest {
        testBandLockNoneNotAllowed(null)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enableLteBandLockingPerSimSlotId0_BANDLOCK_NONE_notAllowed() = runTest {
        testBandLockNoneNotAllowed(0)
    }

    private suspend fun testBandLockNoneNotAllowed(simSlotId: Int?) {
        val band = -1
        val result = EnableBandLockingUseCase(systemManager).invoke(band, simSlotId)
        assertTrue("enableBandLocking API should return an error", result is ApiResult.Error)

        val result2 = GetBandLockingStateUseCase(systemManager).invoke(simSlotId)
        assert(result2 is ApiResult.Success)
        assertTrue("Band locking should be disabled", !(result2 as ApiResult.Success).data.enabled)
        assertTrue("Band locking state should report BAND_LOCKING_NONE", result2.data.value == band)
    }

    @Test
    fun enableLteBandLocking_invalidParameter_returnError() = runTest {
        testInvalidParameter(null)
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enableLteBandLockingPerSimSlotId0_invalidParameter_returnError() = runTest {
        testInvalidParameter(0)
    }

    private suspend fun testInvalidParameter(simSlotId: Int?) {
        val band = -2
        val result = EnableBandLockingUseCase(systemManager).invoke(band, simSlotId)
        assert(result is ApiResult.Error)
    }

    @Test
    @SimRequired
    fun disableLteBandLocking_returnSuccess() = runTest {
        testDisableLteBandLocking(null)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun disableLteBandLockingPerSimSlotId0_returnSuccess() = runTest {
        testDisableLteBandLocking(0)
    }

    private suspend fun testDisableLteBandLocking(simSlotId: Int?) {
        val result = DisableBandLockingUseCase(systemManager).invoke(simSlotId)
        assert(result is ApiResult.Success)

        val result2 = GetBandLockingStateUseCase(systemManager).invoke(simSlotId)
        assert(result2 is ApiResult.Success && !result2.data.enabled)
    }

    @After
    fun cleanup() = runTest {
        if (currentBand == -1) {
            DisableBandLockingUseCase(systemManager).invoke()
        } else {
            EnableBandLockingUseCase(systemManager).invoke(currentBand)
        }
    }
}