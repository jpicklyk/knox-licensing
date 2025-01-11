package net.sfelabs.knoxmoduleshowcase.tests.screen

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.repository.PreferencesRepository
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.api.EnableNightVisionModeFeature
import net.sfelabs.knox_tactical.domain.model.NightVisionState
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 132)
@RunWith(AndroidJUnit4::class)
class NightVisionModeTest {
    private lateinit var feature: EnableNightVisionModeFeature
    //private lateinit var getNightVisionModeStateUseCase: GetNightVisionModeStateUseCase
    //private lateinit var setNightVisionModeStateUseCase: SetNightVisionModeStateUseCase

    private object TestDataStoreSource {
        private val dataStore = mutableMapOf<String, Any>()

        fun <T> setValue(key: String, value: T) {
            dataStore[key] = value as Any
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> getValue(key: String, defaultValue: T): T {
            return dataStore[key] as? T ?: defaultValue
        }

        fun clearAll() {
            dataStore.clear()
        }
    }

    @Before
    fun init() {
        val preferencesRepository = object : PreferencesRepository {
            override suspend fun <T> setValue(key: String, value: T) {
                TestDataStoreSource.setValue(key, value)
            }

            override fun <T> getValue(key: String, defaultValue: T): Flow<T> = flow {
                emit(TestDataStoreSource.getValue(key, defaultValue))
            }
        }

        feature = EnableNightVisionModeFeature(preferencesRepository)
    }

    @After
    fun cleanup() = runTest {
        delay(1000)
        TestDataStoreSource.clearAll()
        cleanupTurnOffNightVisionMode()
    }

    @Test
    fun enableNightVisionMode_noOverlay_returnSuccess() = runTest {
        // Test using feature
        val result = feature.setState(NightVisionState(isEnabled = true, useRedOverlay = false))
        assertTrue(
            "Failed setting night vision mode. Error: ${result.getExceptionOrNull()}",
            result is ApiResult.Success
        )
        delay(1000)

        val stateResult = feature.getState()
        assertTrue(
            "Failed getting night vision mode state. Error: ${stateResult.getErrorOrNull()}",
            stateResult is ApiResult.Success
        )

        val state = (stateResult as ApiResult.Success).data
        assertTrue("Night vision should be enabled", state.isEnabled)
        assertTrue("Red overlay should be disabled", !state.useRedOverlay)
    }

    @Test
    fun enableNightVisionMode_withOverlay_returnSuccess() = runTest {
        val result = feature.setState(NightVisionState(isEnabled = true, useRedOverlay = true))
        assertTrue(
            "Failed setting night vision mode. Error: ${result.getErrorOrNull()}",
            result is ApiResult.Success
        )

        val stateResult = feature.getState()
        assertTrue(
            "Failed getting night vision mode state. Error: ${stateResult.getErrorOrNull()}",
            stateResult is ApiResult.Success
        )

        val state = (stateResult as ApiResult.Success).data
        assertTrue("Night vision should be enabled", state.isEnabled)
        assertTrue("Red overlay should be enabled", state.useRedOverlay)
    }

    @Test
    fun disableNightVisionMode_returnSuccess() = runTest {
        val result = feature.setState(NightVisionState(isEnabled = false, useRedOverlay = false))
        assertTrue(
            "Failed setting night vision mode. Error: ${result.getErrorOrNull()}",
            result is ApiResult.Success
        )

        val stateResult = feature.getState()
        assertTrue(
            "Failed getting night vision mode state. Error: ${stateResult.getErrorOrNull()}",
            stateResult is ApiResult.Success
        )

        val state = (stateResult as ApiResult.Success).data
        assertTrue("Night vision should be disabled", !state.isEnabled)
    }

    @Test
    fun defaultState_shouldBeDisabled() {
        val defaultState = feature.defaultValue
        assertTrue("Default night vision should be disabled", !defaultState.isEnabled)
        assertTrue("Default red overlay should be disabled", !defaultState.useRedOverlay)
    }

    private suspend fun cleanupTurnOffNightVisionMode() {
        feature.setState(NightVisionState(isEnabled = false, useRedOverlay = false))
    }
}
