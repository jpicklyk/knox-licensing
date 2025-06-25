package net.sfelabs.knoxmoduleshowcase.tests.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.radio.IsAlwaysRadioOnEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.SetAlwaysRadioOnEnabledUseCase
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 134)
class AlwaysRadioOnTests {
    private var currentlyEnabled by Delegates.notNull<Boolean>()

    @Before
    fun setup() = runTest {
        val result = IsAlwaysRadioOnEnabledUseCase().invoke()
        if(result is ApiResult.Success) {
            currentlyEnabled = result.data
        }
    }

    @Test
    fun enableAlwaysRadioOn() = runTest {
        val getUseCase = IsAlwaysRadioOnEnabledUseCase()
        val setUseCase = SetAlwaysRadioOnEnabledUseCase()

        val setResult = setUseCase(true)
        assertTrue(
            "API result was not successful, " +
                    "${setResult.getErrorOrNull()}", setResult is ApiResult.Success
        )
        val isEnabled = getUseCase()
        assertTrue(
            "Always radio on is not enabled",
            isEnabled is ApiResult.Success && isEnabled.data
        )
    }

    @Test
    fun disableAlwaysRadioOn() = runTest {
        val getUseCase = IsAlwaysRadioOnEnabledUseCase()
        val setUseCase = SetAlwaysRadioOnEnabledUseCase()

        val setResult = setUseCase(false)
        assertTrue(
            "API result was not successful, " +
                    "${setResult.getErrorOrNull()}", setResult is ApiResult.Success
        )
        val isEnabled = getUseCase()
        assertTrue(
            "Always radio on is still enabled",
            isEnabled is ApiResult.Success && !isEnabled.data
        )
    }

    @After
    fun cleanup() = runTest {
        SetAlwaysRadioOnEnabledUseCase().invoke(currentlyEnabled)
    }
}