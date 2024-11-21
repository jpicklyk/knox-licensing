package net.sfelabs.knox_tactical.domain.use_cases.screen

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.use_case.SetPreferenceUseCase
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError
import javax.inject.Inject

class SetNightVisionModeStateUseCase @Inject constructor(
    //Unique depending on the API we are wrapping.
    private val setPreferenceUseCase: SetPreferenceUseCase<Boolean>,
) : CoroutineApiUseCase<SetNightVisionModeStateUseCase.Params, Unit>() {
    //Unique parameters for this use case that are specific to the API we are wrapping.
    data class Params(
        val enabled: Boolean,
        val useRedOverlay: Boolean = false
    )
    //This function contains unique business logic for the specific API we are wrapping in a use case.
    override suspend fun execute(params: Params): ApiResult<Unit> {
        val systemManager = CustomDeviceManager.getInstance().systemManager
        val result = systemManager.setNightVisionModeState(params.enabled, params.useRedOverlay)

        return when (result) {
            CustomDeviceManager.SUCCESS -> {
                if(params.enabled)
                    setPreferenceUseCase("night_vision_red_overlay_enabled", params.useRedOverlay)
                ApiResult.Success(Unit)
            }
            else -> ApiResult.Error(DefaultApiError.UnexpectedError("Failed to set night vision mode state"))
        }
    }
}