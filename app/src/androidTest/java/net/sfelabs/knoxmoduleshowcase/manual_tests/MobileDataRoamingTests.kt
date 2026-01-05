package net.sfelabs.knoxmoduleshowcase.manual_tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_enterprise.domain.use_cases.SetMobileDataRoamingStateUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MobileDataRoamingTests {

    @Test
    fun disableDataRoaming() = runTest {
        val result = SetMobileDataRoamingStateUseCase().invoke(false)
        assert(result is ApiResult.Success) {
            "Failed to disable data roaming: $result"
        }

    }
}