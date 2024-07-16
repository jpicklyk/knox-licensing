package net.sfelabs.knoxmoduleshowcase.tests.phone

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.calling.GetAutoRecordCallEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.calling.SetAutoRecordCallEnabledUseCase
import net.sfelabs.knoxmoduleshowcase.app.checkMethodExistence
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 110)
class AutoRecordCallTest {
    private val systemManager = KnoxModule.provideKnoxSystemManager()

    @Test
    fun getAutoRecordCallEnabledState_Exists() = runTest {
        val kClass = systemManager::class
        assert(checkMethodExistence(kClass, "getAutomaticRecordCallEnabledState"))
    }

    @Test
    fun setAutoRecordCallEnabledState_Exists() = runTest {
        val kClass = systemManager::class
        assert(checkMethodExistence(kClass, "setAutomaticRecordCallEnabledState"))
    }

    @Test
    fun setAutoRecordCallEnabledState_Enabled() = runTest {
        val useCase = SetAutoRecordCallEnabledUseCase(systemManager)
        val result = useCase.invoke(true)
        assert(result is ApiResult.Success)
        val getCase = GetAutoRecordCallEnabledUseCase(systemManager)
        val result2 = getCase.invoke()
        assert(result2 is ApiResult.Success && result2.data)
    }

    @Test
    fun setAutoRecordCallEnabledState_Disabled() = runTest {
        val useCase = SetAutoRecordCallEnabledUseCase(systemManager)
        val result = useCase.invoke(false)
        assert(result is ApiResult.Success)
        val getCase = GetAutoRecordCallEnabledUseCase(systemManager)
        val result2 = getCase.invoke()
        assert(result2 is ApiResult.Success && !result2.data)
    }

    @After
    fun disableAutoRecordCall() = runTest {
        val useCase = SetAutoRecordCallEnabledUseCase(systemManager)
        useCase.invoke(false)
    }
}