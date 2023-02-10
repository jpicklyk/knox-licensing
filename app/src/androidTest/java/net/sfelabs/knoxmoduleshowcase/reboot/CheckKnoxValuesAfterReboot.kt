package net.sfelabs.knoxmoduleshowcase.reboot

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.domain.use_cases.mtu.GetCustomMtuUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CheckKnoxValuesAfterReboot {
    private val sm = net.sfelabs.knox_tactical.di.KnoxModule.provideKnoxSystemManager()

    @Test
    fun getWlanMtu() = runTest {
        val useCase = GetCustomMtuUseCase(sm)
        val result = useCase.invoke()
        assert(result is ApiCall.Success && result.data == 1430)
    }
}