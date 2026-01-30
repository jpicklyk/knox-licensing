package net.sfelabs.knoxmoduleshowcase.tests.phone

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.data.dto.AutoCallPickupDto
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode
import net.sfelabs.knox_tactical.domain.use_cases.CheckSystemManagerMethodExistsUseCase
import net.sfelabs.knox_tactical.domain.use_cases.calling.GetAutoCallPickupStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.calling.SetAutoCallPickupStateUseCase
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 110)
class AutoCallPickupTests {

    @Test
    fun getAutoCallPickupState_Exists() = runTest {
        val result = CheckSystemManagerMethodExistsUseCase().invoke("getAutoCallPickupState")
        assertTrue(result is ApiResult.Success && result.data)
    }

    @Test
    fun setAutoCallPickupState_Exists() = runTest {
        val result = CheckSystemManagerMethodExistsUseCase().invoke("setAutoCallPickupState")
        assertTrue(result is ApiResult.Success && result.data)
    }

    @Test
    fun setAutoCallPickupState_Enable() = runTest {
        val useCase = SetAutoCallPickupStateUseCase()
        val result = useCase.invoke(AutoCallPickupDto(mode = AutoCallPickupMode.Enable))
        assert(result is ApiResult.Success)

        val getCase = GetAutoCallPickupStateUseCase()
        val res = getCase.invoke()
        assert(res is ApiResult.Success && res.data.mode is AutoCallPickupMode.Enable)
    }

    @Test
    fun setAutoCallPickupState_EnableAlwaysAccept() = runTest {
        val useCase = SetAutoCallPickupStateUseCase()
        val result = useCase.invoke(AutoCallPickupDto(mode = AutoCallPickupMode.EnableAlwaysAccept))
        assertTrue("API call was not successful: ${result.getErrorOrNull()}", result is ApiResult.Success)

        val getCase = GetAutoCallPickupStateUseCase()
        val res = getCase.invoke()
        assert(res is ApiResult.Success && res.data.mode is AutoCallPickupMode.EnableAlwaysAccept)
    }

    @Test
    fun setAutoCallPickupState_Disable() = runTest {
        val useCase = SetAutoCallPickupStateUseCase()
        val result = useCase.invoke(AutoCallPickupDto(mode = AutoCallPickupMode.Disable))
        assert(result is ApiResult.Success)

        val getCase = GetAutoCallPickupStateUseCase()
        val res = getCase.invoke()
        assert(res is ApiResult.Success && res.data.mode is AutoCallPickupMode.Disable)
    }

    @After
    fun disableAutoCallPickupState() = runTest {
        val useCase = SetAutoCallPickupStateUseCase()
        useCase.invoke(AutoCallPickupDto(mode = AutoCallPickupMode.Disable))
    }
}