package net.sfelabs.knoxmoduleshowcase.tests.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.e911.DisableE911OverPrivateNetworksUseCase
import net.sfelabs.knox_tactical.domain.use_cases.e911.IsE911DisabledOverPrivateNetworksUseCase
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 141)
class E911Tests {
    private val disableE911UseCase = DisableE911OverPrivateNetworksUseCase()
    private val isE911DisabledUseCase = IsE911DisabledOverPrivateNetworksUseCase()

    @Test
    fun disableE911OverPrivateNetworks() = runTest {
        val setResult = disableE911UseCase(true)
        assertTrue(
            "Expected disableE911OverPrivateNetworks(true) to return ApiResult.Success but got: $setResult",
            setResult is ApiResult.Success
        )

        val getResult = isE911DisabledUseCase()
        assertTrue(
            "Expected isE911DisabledOverPrivateNetworks to return ApiResult.Success but got: $getResult",
            getResult is ApiResult.Success
        )
        assertTrue(
            "Expected E911 to be disabled over private networks but it is not",
            (getResult as ApiResult.Success).data
        )
    }

    @Test
    fun enableE911OverPrivateNetworks() = runTest {
        val setResult = disableE911UseCase(false)
        assertTrue(
            "Expected disableE911OverPrivateNetworks(false) to return ApiResult.Success but got: $setResult",
            setResult is ApiResult.Success
        )

        val getResult = isE911DisabledUseCase()
        assertTrue(
            "Expected isE911DisabledOverPrivateNetworks to return ApiResult.Success but got: $getResult",
            getResult is ApiResult.Success
        )
        assertTrue(
            "Expected E911 to be enabled over private networks but it is still disabled",
            !(getResult as ApiResult.Success).data
        )
    }

    @After
    fun cleanup() = runTest {
        disableE911UseCase(false)
    }
}
