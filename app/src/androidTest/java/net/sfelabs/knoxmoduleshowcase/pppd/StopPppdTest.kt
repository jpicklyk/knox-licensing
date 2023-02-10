package net.sfelabs.knoxmoduleshowcase.pppd

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_common.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.adb.StopPppdUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StopPppdTest {

    @Test
    fun testStopPppd() = runBlocking {
        val sm = KnoxModule.provideKnoxSystemManager()
        val useCase = StopPppdUseCase(sm)
        val result = useCase.invoke()
        if(result is ApiCall.Error) {
            println(result.uiText)
        }
        assert(result is ApiCall.Success)
    }
}