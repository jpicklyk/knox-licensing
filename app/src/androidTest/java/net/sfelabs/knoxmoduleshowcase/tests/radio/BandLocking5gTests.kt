package net.sfelabs.knoxmoduleshowcase.tests.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox.core.testing.rules.CarrierDataRequired
import net.sfelabs.knox.core.testing.rules.SimMustBeRemovedRule
import net.sfelabs.knox.core.testing.rules.SimRemoved
import net.sfelabs.knox.core.testing.rules.SimRequired
import net.sfelabs.knox.core.testing.rules.SimRequiredRule
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.radio.Disable5gBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Enable5gBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Get5gBandLockingUseCase
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 131)
class BandLocking5gTests {
    @get:Rule
    val simRequiredRule = SimRequiredRule()
    @get:Rule
    val simMustBeRemovedRule = SimMustBeRemovedRule()


    @Test
    @SimRemoved
    fun disable5gBandLocking_withNoSim_shouldReturnError() = runTest {
        val result = Disable5gBandLockingUseCase().invoke(null)
        assertTrue(
            "disableBandLocking API should return error when there is no sim card present",
            result is ApiResult.Error
        )
    }

    @Test
    @SimRemoved
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun disable5gBandLocking_forSimSlot0_withNoSim_shouldReturnError() = runTest {
        val result = Disable5gBandLockingUseCase().invoke(0)
        assertTrue(
            "disableBandLocking API should return error when there is no sim card present for simSlotId 0",
            result is ApiResult.Error
        )
    }

    @Test
    @SimRemoved
    fun enable5gBandLocking_withNoSim_shouldReturnError() = runTest {
        val result = Enable5gBandLockingUseCase().invoke(78)
        assertTrue("enable5gBandLocking API should return an error", result is ApiResult.Error)
    }

    @Test
    @SimRemoved
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enable5gBandLocking_forSimSlot0_withNoSim_shouldReturnError() = runTest {
        val result = Enable5gBandLockingUseCase().invoke(78, 0)
        assertTrue("enable5gBandLocking API should return an error for simSlotId 0", result is ApiResult.Error)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enable5gBandLocking_forInvalidSimSlot1_shouldReturnError() = runTest {
        val result = Enable5gBandLockingUseCase().invoke(78, 1)
        assertTrue("enable5gBandLocking API should return an error for simSlotId 1 when there is no eSIM installed.", result is ApiResult.Error)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun disable5gBandLocking_forInvalidSimSlot1_shouldReturnError() = runTest {
        val result = Disable5gBandLockingUseCase().invoke(1)
        assertTrue("disable5gBandLocking API should return an error for simSlotId 1 when there is no eSIM installed.", result is ApiResult.Error)
    }

    @Test
    @SimRequired
    fun enable5gBandLocking_forBand78_shouldSucceed() = runTest {
        testEnableBandLocking(null)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enable5gBandLocking_forSimSlot0AndBand78_shouldSucceedAndNotAffectOtherSlots() = runTest {
        testEnableBandLocking(0)

        //confirm that SIM id 1 isn't also enabled
        val result = Get5gBandLockingUseCase().invoke(1)
        assert(result is ApiResult.Success)
        val state = result.getOrNull()
        assertTrue("Band locking on SIM id 1 should not be enabled when configuring SIM 0.", state?.band == -1)

        //confirm SIM id 1 does not have the band applied
        assertTrue("SIM id 1 should be returning -1 not ${state}.", state?.band == -1)
    }

    private suspend fun testEnableBandLocking(simSlotId: Int?) {
        val band = 78
        val result = Enable5gBandLockingUseCase().invoke(band, simSlotId)
        assertTrue("enable5gBandLocking API should return success.  Error: $result",result is ApiResult.Success)

        val result2 = Get5gBandLockingUseCase().invoke(simSlotId)
        assert(result2 is ApiResult.Success && result2.data.band == band)
    }

    @Test
    @SimRequired
    fun enable5gBandLocking_withNegativeOneBand_shouldReturnError() = runTest {
        testBandLockNoneNotAllowed(null)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enable5gBandLocking_forSimSlot0WithNegativeOneBand_shouldReturnError() = runTest {
        testBandLockNoneNotAllowed(0)
    }

    private suspend fun testBandLockNoneNotAllowed(simSlotId: Int?) {
        val band = -1
        val result = Enable5gBandLockingUseCase().invoke(band, simSlotId)
        assertTrue("enable5gBandLocking API should return an error", result is ApiResult.Error)

        val result2 = Get5gBandLockingUseCase().invoke(simSlotId)
        assert(result2 is ApiResult.Success)
        assertTrue("Band locking should be disabled", (result2 as ApiResult.Success).data.band == -1)
        assertTrue("Band locking state should report BAND_LOCKING_NONE", result2.data.band == band)
    }

    @Test
    fun enable5gBandLocking_withInvalidBandValue_shouldReturnError() = runTest {
        testInvalidParameter(null)
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enable5gBandLocking_forSimSlot0WithInvalidBandValue_shouldReturnError() = runTest {
        testInvalidParameter(0)
    }

    private suspend fun testInvalidParameter(simSlotId: Int?) {
        val band = -2
        val result = Enable5gBandLockingUseCase().invoke(band, simSlotId)
        assert(result is ApiResult.Error)
    }

    @Test
    @SimRequired
    fun disable5gBandLocking_shouldSucceed() = runTest {
        testDisableBandLocking(null)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun disable5gBandLocking_forSimSlot0_shouldSucceed() = runTest {
        testDisableBandLocking(0)
    }

    private suspend fun testDisableBandLocking(simSlotId: Int?) {
        val result = Disable5gBandLockingUseCase().invoke(simSlotId)
        assertTrue("disable5GBandLocking API should return success.  Error: $result",result is ApiResult.Success)

        val result2 = Get5gBandLockingUseCase().invoke(simSlotId)
        assert(result2 is ApiResult.Success && result2.data.band == -1)
    }

    @After
    fun disableAllBandLocking() = runTest {
        for(i in 0..2) {
            Disable5gBandLockingUseCase().invoke(i)
        }
    }
}