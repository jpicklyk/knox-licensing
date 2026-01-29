package net.sfelabs.knoxmoduleshowcase.manual_tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_enterprise.domain.use_cases.SetMobileDataStateUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MobileDataTests {

    @Test
    fun enableMobileData() = runTest {
        val useCase = SetMobileDataStateUseCase()
        val result = useCase.invoke(true)
        assert(result is ApiResult.Success)
    }
}