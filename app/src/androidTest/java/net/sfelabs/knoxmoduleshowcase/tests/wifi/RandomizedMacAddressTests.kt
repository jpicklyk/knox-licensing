package net.sfelabs.knoxmoduleshowcase.tests.wifi

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox_enterprise.domain.use_cases.CheckRestrictionPolicyMethodExistsUseCase
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.wifi.EnableRandomizedMacAddressUseCase
import net.sfelabs.knox_tactical.domain.use_cases.wifi.GetRandomizedMacAddressEnabledUseCase
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 130)
class RandomizedMacAddressTests {

    @Test
    @ApiExists
    fun isRandomisedMacAddressEnabled_Exists() = runTest {
        val result = CheckRestrictionPolicyMethodExistsUseCase().invoke("isRandomisedMacAddressEnabled")
        assertTrue(result is ApiResult.Success && result.data)
    }

    @Test
    @ApiExists
    fun enableRandomisedMacAddress_Exists() = runTest {
        val result = CheckRestrictionPolicyMethodExistsUseCase().invoke("enableRandomisedMacAddress")
        assertTrue(result is ApiResult.Success && result.data)
    }

    @Test
    fun testGetRandomizedMacAddressApi() = runTest {
        val useCase = GetRandomizedMacAddressEnabledUseCase()
        val result = useCase.invoke()
        assert(result is ApiResult.Success)
    }

    @Test
    fun testDisableRandomizedMacAddress() = runTest {
        val useCase = EnableRandomizedMacAddressUseCase()
        val result = useCase.invoke(false)
        assert(result is ApiResult.Success)
        val useCase2 = GetRandomizedMacAddressEnabledUseCase()
        val result2 = useCase2.invoke()
        assert(result2 is ApiResult.Success && !result2.data )
    }

    @Test
    fun testEnableRandomizedMacAddress() = runTest {
        val useCase = EnableRandomizedMacAddressUseCase()
        val result = useCase.invoke(true)
        assert(result is ApiResult.Success)
        val useCase2 = GetRandomizedMacAddressEnabledUseCase()
        val result2 = useCase2.invoke()
        assert(result2 is ApiResult.Success && result2.data )
    }

    @After
    fun cleanup() = runTest {
        EnableRandomizedMacAddressUseCase().invoke(true)
    }
}