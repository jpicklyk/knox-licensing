package net.sfelabs.knoxmoduleshowcase.manual_tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.runBlocking
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.knox_common.di.KnoxModule
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.adb.StopPppdUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 112)
class StopPppdTest {

    @Test
    fun testStopPppd() = runBlocking {
        val sm = KnoxModule.provideKnoxSystemManager()
        val useCase = StopPppdUseCase(sm)
        val result = useCase.invoke()
        if(result is ApiResult.Error) {
            println(result.apiError.message)
        }
        assert(result is ApiResult.Success)
    }
}