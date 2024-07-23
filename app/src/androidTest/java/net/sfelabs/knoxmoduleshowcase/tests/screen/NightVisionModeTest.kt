package net.sfelabs.knoxmoduleshowcase.tests.screen

import androidx.test.filters.SmallTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.data.datasource.DataStoreSource
import net.sfelabs.core.di.PreferencesModule
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.screen.GetNightVisionModeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.screen.GetNightVisionRedOverlayUseCase
import net.sfelabs.knox_tactical.domain.use_cases.screen.SetNightVisionModeStateUseCase
import net.sfelabs.knoxmoduleshowcase.di.TestDataStoreDataSourceImpl
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(PreferencesModule::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 132)
class NightVisionModeTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var testDataStoreDataSource: DataStoreSource

    @Before
    fun init() {
        hiltRule.inject()
    }
    @Inject
    lateinit var setNightVisionModeStateUseCase: SetNightVisionModeStateUseCase
    @Inject
    lateinit var getNightVisionModeUseCase: GetNightVisionModeUseCase
    @Inject
    lateinit var getNightVisionRedOverlayUseCase: GetNightVisionRedOverlayUseCase

    @Test
    fun enableNightVisionMode_noOverlay_returnSuccess() = runTest {
        val params = SetNightVisionModeStateUseCase.Params(enabled = true, useRedOverlay = false)
        val result = setNightVisionModeStateUseCase(params)
        assertTrue(
            "Failed setting night vision mode.  Error: ${result.getExceptionOrNull()}",
            result is ApiResult.Success
        )
        delay(1000)

        val result2 = getNightVisionModeUseCase()
        assertTrue(
            "Failed getting night vision mode.  Error: ${result2.getErrorOrNull()}",
            result2 is ApiResult.Success)
    }

    @Test
    fun enableNightVisionMode_withOverlay_returnSuccess() = runTest {
        val params = SetNightVisionModeStateUseCase.Params(enabled = true, useRedOverlay = true)
        val result = setNightVisionModeStateUseCase(params)
        assertTrue(
            "Failed setting night vision mode.  Error: ${result.getErrorOrNull()}",
            result is ApiResult.Success
        )

        val result2 = getNightVisionModeUseCase()
        assertTrue(
            "Failed getting night vision mode.  Error: ${result2.getErrorOrNull()}",
            result2 is ApiResult.Success)

        val result3 = getNightVisionRedOverlayUseCase()
        assertTrue(
            "Failed getting night vision red overlay.  Error: ${result3.getErrorOrNull()}",
            (result3 as ApiResult.Success).data
        )
    }

    @Test
    fun disableNightVisionMode_returnSuccess() = runTest {
        val params = SetNightVisionModeStateUseCase.Params(enabled = false)
        val result = setNightVisionModeStateUseCase(params)
        assertTrue(
            "Failed setting night vision mode.  Error: ${result.getErrorOrNull()}",
            result is ApiResult.Success
        )

        val result2 = getNightVisionModeUseCase()
        assertTrue(
            "Failed getting night vision mode.  Error: ${result2.getErrorOrNull()}",
            result2 is ApiResult.Success)
    }

    @After
    fun cleanup() = runTest {
        delay(1000)
        (testDataStoreDataSource as TestDataStoreDataSourceImpl).clearAll()
        cleanupTurnOffNightVisionMode()
    }

    private suspend fun cleanupTurnOffNightVisionMode() {
        val params = SetNightVisionModeStateUseCase.Params(enabled = false)
        setNightVisionModeStateUseCase(params)
    }

}