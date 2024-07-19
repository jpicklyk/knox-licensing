package net.sfelabs.knox_tactical.domain.use_cases.screen

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.use_case.CoroutineApiUseCase
import net.sfelabs.core.domain.use_case.SetPreferenceUseCase
import javax.inject.Inject

class SetNightVisionModeStateUseCase @Inject constructor(
    private val setPreferenceUseCase: SetPreferenceUseCase<Boolean>,
) : CoroutineApiUseCase<SetNightVisionModeStateUseCase.Params, Unit>() {
    data class Params(
        val enabled: Boolean,
        val useRedOverlay: Boolean = false
    )

    override suspend fun execute(params: Params): ApiResult<Unit> {
        val systemManager = CustomDeviceManager.getInstance().systemManager
        val result = systemManager.setNightVisionModeState(params.enabled, params.useRedOverlay)

        return when (result) {
            CustomDeviceManager.SUCCESS -> {
                if(params.enabled)
                    setPreferenceUseCase("night_vision_red_overlay_enabled", params.useRedOverlay)
                ApiResult.Success(Unit)
            }
            else -> ApiResult.Error(UiText.DynamicString("Failed to set night vision mode state"))
        }
    }
}