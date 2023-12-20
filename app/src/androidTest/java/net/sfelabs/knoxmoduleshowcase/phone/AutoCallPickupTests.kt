package net.sfelabs.knoxmoduleshowcase.phone

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupState
import net.sfelabs.knox_tactical.domain.use_cases.calling.GetAutoCallPickupStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.calling.SetAutoCallPickupStateUseCase
import net.sfelabs.knoxmoduleshowcase.app.checkMethodExistence
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 110)
class AutoCallPickupTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()

    @Test
    fun getAutoCallPickupState_Exists() = runTest {
        val kClass = systemManager::class
        assert(checkMethodExistence(kClass, "getAutoCallPickupState"))
    }

    @Test
    fun setAutoCallPickupState_Exists() = runTest {
        val kClass = systemManager::class
        assert(checkMethodExistence(kClass, "setAutoCallPickupState"))
    }

    @Test
    fun setAutoCallPickupState_Enable() = runTest {
        val useCase = SetAutoCallPickupStateUseCase(systemManager)
        val result = useCase.invoke(AutoCallPickupState.Enable)
        assert(result is ApiCall.Success)

        val getCase = GetAutoCallPickupStateUseCase(systemManager)
        val res = getCase.invoke()
        assert(res is ApiCall.Success && res.data is AutoCallPickupState.Enable)
    }

    @Test
    fun setAutoCallPickupState_EnableAlwaysAccept() = runTest {
        val useCase = SetAutoCallPickupStateUseCase(systemManager)
        val result = useCase.invoke(AutoCallPickupState.EnableAlwaysAccept)
        assert(result is ApiCall.Success)

        val getCase = GetAutoCallPickupStateUseCase(systemManager)
        val res = getCase.invoke()
        assert(res is ApiCall.Success && res.data is AutoCallPickupState.EnableAlwaysAccept)
    }

    @Test
    fun setAutoCallPickupState_Disable() = runTest {
        val useCase = SetAutoCallPickupStateUseCase(systemManager)
        val result = useCase.invoke(AutoCallPickupState.Disable)
        assert(result is ApiCall.Success)

        val getCase = GetAutoCallPickupStateUseCase(systemManager)
        val res = getCase.invoke()
        assert(res is ApiCall.Success && res.data is AutoCallPickupState.Disable)
    }

    @After
    fun disableAutoCallPickupState() = runTest {
        val useCase = SetAutoCallPickupStateUseCase(systemManager)
        useCase.invoke(AutoCallPickupState.Disable)
    }
}