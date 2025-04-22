package net.sfelabs.knoxmoduleshowcase.tests.pogo

import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.pogo.DisablePogoKeyboardConnectionUseCase
import net.sfelabs.knox_tactical.domain.use_cases.pogo.IsPogoKeyboardConnectionDisabledUseCase
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test

@SmallTest
@TacticalSdkSuppress(includeModels = ["SM-X308U"])
class PogoTests {

    @Test
    fun disablePogoKeyboardConnection() = runTest {
        val getUseCase = IsPogoKeyboardConnectionDisabledUseCase()
        val setUseCase = DisablePogoKeyboardConnectionUseCase()

        val setResult = setUseCase(true)
        assertTrue(
            "API result was not successful, " +
                    "${setResult.getErrorOrNull()}", setResult is ApiResult.Success
        )
        val isDisabled = getUseCase()
        assertTrue(
            "Pogo keyboard connection is enabled",
            isDisabled is ApiResult.Success && isDisabled.data
        )
    }

    @Test
    fun enablePogoKeyboardConnection() = runTest {
        val getUseCase = IsPogoKeyboardConnectionDisabledUseCase()
        val setUseCase = DisablePogoKeyboardConnectionUseCase()

        val setResult = setUseCase(false)
        assertTrue(
            "API result was not successful, " +
                    "${setResult.getErrorOrNull()}", setResult is ApiResult.Success
        )
        val isDisabled = getUseCase()
        assertTrue(
            "Pogo keyboard connection is disabled",
            isDisabled is ApiResult.Success && !isDisabled.data
        )
    }

    @After
    fun resetDefaults() = enablePogoKeyboardConnection()
}