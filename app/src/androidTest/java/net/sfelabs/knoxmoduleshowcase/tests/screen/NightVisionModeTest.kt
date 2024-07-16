package net.sfelabs.knoxmoduleshowcase.tests.screen

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.screen.GetNightVisionModeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.screen.SetNightVisionModeStateUseCase
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 132)
class NightVisionModeTest {

    @Test
    fun enableNightVisionMode_noOverlay_returnSuccess() = runTest {
        val params = SetNightVisionModeStateUseCase.Params(enabled = true, useRedOverlay = false)
        val result = SetNightVisionModeStateUseCase().invoke(params)
        assertTrue(
            "Failed setting night vision mode.  Error: ${result.getErrorOrNull()}",
            result is ApiResult.Success
        )

        val result2 = GetNightVisionModeUseCase().invoke()
        assertTrue(
            "Failed getting night vision mode.  Error: ${result2.getErrorOrNull()}",
            result2 is ApiResult.Success)
    }

    @Test
    fun enableNightVisionMode_withOverlay_returnSuccess() = runTest {
        val params = SetNightVisionModeStateUseCase.Params(enabled = true, useRedOverlay = true)
        val result = SetNightVisionModeStateUseCase().invoke(params)
        assertTrue(
            "Failed setting night vision mode.  Error: ${result.getErrorOrNull()}",
            result is ApiResult.Success
        )

        val result2 = GetNightVisionModeUseCase().invoke()
        assertTrue(
            "Failed getting night vision mode.  Error: ${result2.getErrorOrNull()}",
            result2 is ApiResult.Success)
    }

    @Test
    fun disableNightVisionMode_returnSuccess() = runTest {
        val params = SetNightVisionModeStateUseCase.Params(enabled = false)
        val result = SetNightVisionModeStateUseCase().invoke(params)
        assertTrue(
            "Failed setting night vision mode.  Error: ${result.getErrorOrNull()}",
            result is ApiResult.Success
        )

        val result2 = GetNightVisionModeUseCase().invoke()
        assertTrue(
            "Failed getting night vision mode.  Error: ${result2.getErrorOrNull()}",
            result2 is ApiResult.Success)
    }

    @After
    fun cleanup_turnOffNightVisionMode() = runTest {
        val params = SetNightVisionModeStateUseCase.Params(enabled = false)
        //SetNightVisionModeStateUseCase().invoke(params)
    }

}