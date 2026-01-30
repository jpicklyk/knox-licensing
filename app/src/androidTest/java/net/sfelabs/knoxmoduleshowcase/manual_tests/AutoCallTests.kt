package net.sfelabs.knoxmoduleshowcase.manual_tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.data.dto.AutoCallPickupDto
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode
import net.sfelabs.knox_tactical.domain.use_cases.calling.AddAutoCallNumberUseCase
import net.sfelabs.knox_tactical.domain.use_cases.calling.AnswerMode
import net.sfelabs.knox_tactical.domain.use_cases.calling.GetAutoCallNumberListUseCase
import net.sfelabs.knox_tactical.domain.use_cases.calling.SetAutoCallPickupStateUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 110)
class AutoCallTests {

    @Test
    fun checkActivationInfo() = runTest {
        // Add auto-call number
        val addResult = AddAutoCallNumberUseCase().invoke(
            phoneNumber = "15192407948",
            priority = 2,
            answerMode = AnswerMode.SPEAKER
        )
        assert(addResult is ApiResult.Success) { "Failed to add auto-call number: ${addResult.getErrorOrNull()}" }

        // Get list and verify
        val listResult = GetAutoCallNumberListUseCase().invoke()
        assert(listResult is ApiResult.Success && listResult.data.isNotEmpty()) {
            "Auto-call number list is empty or failed: ${listResult.getErrorOrNull()}"
        }

        // Enable auto-call pickup
        val setResult = SetAutoCallPickupStateUseCase().invoke(
            AutoCallPickupDto(mode = AutoCallPickupMode.Enable)
        )
        assert(setResult is ApiResult.Success) { "Failed to set auto-call pickup state: ${setResult.getErrorOrNull()}" }
    }
}