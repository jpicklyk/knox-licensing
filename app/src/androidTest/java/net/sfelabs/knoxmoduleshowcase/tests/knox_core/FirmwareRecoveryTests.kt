package net.sfelabs.knoxmoduleshowcase.tests.knox_core

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_common.di.KnoxModule
import net.sfelabs.knox_common.domain.use_cases.AllowFirmwareRecoveryUseCase
import net.sfelabs.knox_common.domain.use_cases.IsFirmwareRecoveryAllowedUseCase
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SmallTest
class FirmwareRecoveryTests {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun test1_AllowRecovery() = runTest {
        val edm = KnoxModule.provideKnoxEnterpriseDeviceManager(context)
        val useCase = AllowFirmwareRecoveryUseCase(edm)
        val result = useCase.invoke(true)
        assert(result is ApiResult.Success)

        val allowed = IsFirmwareRecoveryAllowedUseCase(edm).invoke(true)
        assert(allowed is ApiResult.Success && allowed.data)
    }

    @Test
    fun test3_DisableRecovery() = runTest {
        val edm = KnoxModule.provideKnoxEnterpriseDeviceManager(context)
        val useCase = AllowFirmwareRecoveryUseCase(edm)
        val result = useCase.invoke(false)
        assert(result is ApiResult.Success)

        val allowed = IsFirmwareRecoveryAllowedUseCase(edm).invoke(true)
        assert(allowed is ApiResult.Success && !allowed.data)
    }

    @After
    fun test5Cleanup() = runTest {
        val edm = KnoxModule.provideKnoxEnterpriseDeviceManager(context)
        AllowFirmwareRecoveryUseCase(edm).invoke(true)
    }
}