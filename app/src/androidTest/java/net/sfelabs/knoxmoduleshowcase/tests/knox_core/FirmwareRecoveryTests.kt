package net.sfelabs.knoxmoduleshowcase.tests.knox_core

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_enterprise.domain.use_cases.AllowFirmwareRecoveryUseCase
import net.sfelabs.knox_enterprise.domain.use_cases.IsFirmwareRecoveryAllowedUseCase
import org.junit.After
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SmallTest
class FirmwareRecoveryTests {

    @Test
    fun test1_AllowRecovery() = runTest {
        val useCase = AllowFirmwareRecoveryUseCase()
        val result = useCase.invoke(true)
        assert(result is ApiResult.Success)

        val allowed = IsFirmwareRecoveryAllowedUseCase().invoke(true)
        assert(allowed is ApiResult.Success && allowed.data)
    }

    @Test
    fun test3_DisableRecovery() = runTest {
        val useCase = AllowFirmwareRecoveryUseCase()
        val result = useCase.invoke(false)
        assert(result is ApiResult.Success)

        val allowed = IsFirmwareRecoveryAllowedUseCase().invoke(true)
        assert(allowed is ApiResult.Success && !allowed.data)
    }

    @After
    fun test5Cleanup() = runTest {
        AllowFirmwareRecoveryUseCase().invoke(true)
    }
}