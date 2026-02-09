package net.sfelabs.knoxmoduleshowcase.tests.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox.core.testing.rules.SimMustBeRemovedRule
import net.sfelabs.knox.core.testing.rules.SimRemoved
import net.sfelabs.knox.core.testing.rules.SimRequired
import net.sfelabs.knox.core.testing.rules.SimRequiredRule
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.radio.DisableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.EnableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.GetBandLockingStateUseCase
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 100)
class BandLockingLteTests {
    @get:Rule
    val simRequiredRule = SimRequiredRule()
    @get:Rule
    val simMustBeRemovedRule = SimMustBeRemovedRule()


//    @Test
//    @SimRemoved
//    fun disableBandLocking_withNoSim_shouldReturnError() = runTest {
//        testDisableBandLockingSimRemoved(null)
//    }
//
//    @Test
//    @SimRequired
//    @TacticalSdkSuppress(minReleaseVersion = 132)
//    fun enableBandLocking_forInvalidSimSlot1_shouldReturnError() = runTest {
//        val result = EnableBandLockingUseCase().invoke(78, 1)
//        assertTrue("enableBandLocking API should return an error for simSlotId 1 when there is no eSIM installed.", result is ApiResult.Error)
//    }
//
//    @Test
//    @SimRequired
//    @TacticalSdkSuppress(minReleaseVersion = 132)
//    fun disableBandLocking_forInvalidSimSlot1_shouldReturnError() = runTest {
//        val result = DisableBandLockingUseCase().invoke(1)
//        assertTrue("disableBandLocking API should return an error for simSlotId 1 when there is no eSIM installed.", result is ApiResult.Error)
//    }
//
//    @Test
//    @SimRemoved
//    @TacticalSdkSuppress(minReleaseVersion = 132)
//    fun disableBandLocking_forSimSlot0_withNoSim_shouldReturnError() = runTest {
//        testDisableBandLockingSimRemoved(0)
//    }

    private suspend fun testDisableBandLockingSimRemoved(simSlotId: Int?) {
        val result = DisableBandLockingUseCase().invoke(simSlotId)
        assertTrue(
            "disableBandLocking API should return an error when there is no sim card present",
            result is ApiResult.Error
        )
    }

//    @Test
//    @SimRemoved
//    fun enableBandLocking_withNoSim_shouldReturnError() = runTest {
//        testEnableBandLockingSimRemoved(null)
//    }
//
//    @Test
//    @SimRemoved
//    @TacticalSdkSuppress(minReleaseVersion = 132)
//    fun enableBandLocking_forSimSlot0_withNoSim_shouldReturnError() = runTest {
//        testEnableBandLockingSimRemoved(0)
//    }

    private suspend fun testEnableBandLockingSimRemoved(simSlotId: Int?) {
        val band = 78
        val result = EnableBandLockingUseCase().invoke(band, simSlotId)
        assertTrue("enableBandLocking API should return an error", result is ApiResult.Error)
    }

    // Note: Using explicit simSlotId=0 because Knox SDK's non-per-slot API
    // internally targets simSlotId=1, causing getter/setter mismatch
    @Test
    @SimRequired
    fun enableBandLocking_forBand8_shouldSucceed() = runTest {
        testEnableLteBandLocking(0)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enableBandLocking_forSimSlot0AndBand8_shouldSucceedAndNotAffectOtherSlots() = runTest {
        testEnableLteBandLocking(0)
        //confirm that SIM id 1 isn't also enabled
        val result = GetBandLockingStateUseCase().invoke(1)
        assert(result is ApiResult.Success)
        val state = result.getOrNull()
        assertTrue("Band locking on SIM id 1 should not be enabled when configuring SIM 0.", state?.band == -1)

        //confirm SIM id 1 does not have the band applied
        assertTrue("SIM id 1 should be returning -1 not ${state}.", state?.band == -1)
    }

    private suspend fun testEnableLteBandLocking(simSlotId: Int?) {
        val band = 8
        val result = EnableBandLockingUseCase().invoke(band, simSlotId)
        assertTrue("LTE band lock was unsuccessful: ${result.getErrorOrNull()}", result is ApiResult.Success)

        val result2 = GetBandLockingStateUseCase().invoke(simSlotId)
        assertTrue(
            "GetBandLockingState failed or returned wrong band. Expected band=$band, got: $result2",
            result2 is ApiResult.Success && result2.data.band == band
        )
    }

//    @Test
//    @SimRequired
//    fun enableBandLocking_withNegativeOneBand_shouldReturnError() = runTest {
//        testBandLockNoneNotAllowed(null)
//    }
//
//    @Test
//    @SimRequired
//    @TacticalSdkSuppress(minReleaseVersion = 132)
//    fun enableBandLocking_forSimSlot0WithNegativeOneBand_shouldReturnError() = runTest {
//        testBandLockNoneNotAllowed(0)
//    }

    private suspend fun testBandLockNoneNotAllowed(simSlotId: Int?) {
        val band = -1
        val result = EnableBandLockingUseCase().invoke(band, simSlotId)
        assertTrue("enableBandLocking API should return an error", result is ApiResult.Error)

        val result2 = GetBandLockingStateUseCase().invoke(simSlotId)
        assert(result2 is ApiResult.Success)
        assertTrue("Band locking should be disabled", (result2 as ApiResult.Success).data.band == -1)
        assertTrue("Band locking state should report BAND_LOCKING_NONE", result2.data.band == band)
    }

//    @Test
//    fun enableBandLocking_withInvalidBandValue_shouldReturnError() = runTest {
//        testInvalidParameter(null)
//    }
//
//    @Test
//    @TacticalSdkSuppress(minReleaseVersion = 132)
//    fun enableBandLocking_forSimSlot0WithInvalidBandValue_shouldReturnError() = runTest {
//        testInvalidParameter(0)
//    }

    private suspend fun testInvalidParameter(simSlotId: Int?) {
        val band = -2
        val result = EnableBandLockingUseCase().invoke(band, simSlotId)
        assert(result is ApiResult.Error)
    }

    // Note: Using explicit simSlotId=0 because Knox SDK's non-per-slot API
    // internally targets simSlotId=1, causing getter/setter mismatch
    @Test
    @SimRequired
    fun disableBandLocking_shouldSucceed() = runTest {
        testDisableLteBandLocking(0)
    }

    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun disableBandLocking_forSimSlot0_shouldSucceed() = runTest {
        testDisableLteBandLocking(0)
    }

    private suspend fun testDisableLteBandLocking(simSlotId: Int?) {
        val result = DisableBandLockingUseCase().invoke(simSlotId)
        assert(result is ApiResult.Success)

        val result2 = GetBandLockingStateUseCase().invoke(simSlotId)
        assert(result2 is ApiResult.Success && result2.data.band == -1)
    }

    /**
     * Test that setting band locking on slot 0 does NOT affect slot 1.
     */
    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enableBandLocking_forSlot0_shouldNotAffectSlot1() = runTest {
        val band = 8

        // First, ensure both slots are cleared
        DisableBandLockingUseCase().invoke(0)
        DisableBandLockingUseCase().invoke(1)

        // Enable band locking on slot 0 only
        val enableResult = EnableBandLockingUseCase().invoke(band, 0)
        println("EnableBandLocking(band=$band, simSlotId=0) result: $enableResult")
        assertTrue("EnableBandLocking for slot 0 failed: ${enableResult.getErrorOrNull()}", enableResult is ApiResult.Success)

        // Read back from slot 0 - should have band set
        val slot0Result = GetBandLockingStateUseCase().invoke(0)
        println("GetBandLockingState(simSlotId=0): $slot0Result")
        assertTrue(
            "Slot 0 should have band=$band, got: $slot0Result",
            slot0Result is ApiResult.Success && slot0Result.data.band == band
        )

        // Read back from slot 1 - should NOT have band set (should be -1)
        val slot1Result = GetBandLockingStateUseCase().invoke(1)
        println("GetBandLockingState(simSlotId=1): $slot1Result")
        assertTrue(
            "Slot 1 should NOT be affected (expected band=-1), got: $slot1Result",
            slot1Result is ApiResult.Success && slot1Result.data.band == -1
        )
    }

    /**
     * Diagnostic test to investigate Knox SDK behavior when using non-per-slot API.
     * enableLteBandLocking(band) with null simSlotId should auto-apply to slot 0,
     * but Knox logs show it targets slot 1 internally.
     */
    @Test
    @SimRequired
    @TacticalSdkSuppress(minReleaseVersion = 132)
    fun enableBandLocking_withNullSimSlot_shouldApplyToSlot0() = runTest {
        val band = 8

        // First, ensure both slots are cleared
        DisableBandLockingUseCase().invoke(0)
        DisableBandLockingUseCase().invoke(1)

        // Call enable with null (uses non-per-slot API: enableLteBandLocking)
        val enableResult = EnableBandLockingUseCase().invoke(band, null)
        println("EnableBandLocking(band=$band, simSlotId=null) result: $enableResult")

        // Read back from slot 0 explicitly
        val slot0Result = GetBandLockingStateUseCase().invoke(0)
        println("GetBandLockingState(simSlotId=0): $slot0Result")

        // Read back from slot 1 explicitly
        val slot1Result = GetBandLockingStateUseCase().invoke(1)
        println("GetBandLockingState(simSlotId=1): $slot1Result")

        // Read back with null (uses non-per-slot getter: getLteBandLocking)
        val nullResult = GetBandLockingStateUseCase().invoke(null)
        println("GetBandLockingState(simSlotId=null): $nullResult")

        // Assert: enable should succeed
        assertTrue("EnableBandLocking failed: ${enableResult.getErrorOrNull()}", enableResult is ApiResult.Success)

        // Assert: slot 0 should have the band set (expected behavior)
        val slot0Band = (slot0Result as? ApiResult.Success)?.data?.band
        val slot1Band = (slot1Result as? ApiResult.Success)?.data?.band
        val nullBand = (nullResult as? ApiResult.Success)?.data?.band

        println("Summary: slot0=$slot0Band, slot1=$slot1Band, null=$nullBand")

        assertTrue(
            "Expected band=$band on slot 0, but got slot0=$slot0Band, slot1=$slot1Band, null=$nullBand",
            slot0Band == band
        )
    }

    @After
    fun disableAllBandLocking() = runTest {
        for(i in 0..2) {
            DisableBandLockingUseCase().invoke(i)
        }
    }
}