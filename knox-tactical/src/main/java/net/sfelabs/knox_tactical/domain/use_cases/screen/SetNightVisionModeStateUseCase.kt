package net.sfelabs.knox_tactical.domain.use_cases.screen

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.repository.PreferencesRepository
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.domain.model.NightVisionState

class SetNightVisionModeStateUseCase(
    private val preferenceRepository: PreferencesRepository = PreferencesRepository.getInstance()
): CoroutineApiUseCase<NightVisionState, Unit>() {
    // Quality of life invoke function
    suspend operator fun invoke(enable: Boolean, useRedOverlay: Boolean = false): UnitApiCall {
        return invoke(NightVisionState(enable, useRedOverlay))
    }

    //This function contains unique business logic for the specific API we are wrapping in a use case.
    override suspend fun execute(params: NightVisionState): ApiResult<Unit> {
        val systemManager = CustomDeviceManager.getInstance().systemManager
        val result = systemManager.setNightVisionModeState(params.isEnabled, params.useRedOverlay)

        return when (result) {
            CustomDeviceManager.SUCCESS -> {
                if(params.isEnabled)
                    preferenceRepository.setValue("night_vision_red_overlay_enabled", params.useRedOverlay)
                ApiResult.Success(Unit)
            }
            else -> ApiResult.Error(DefaultApiError.UnexpectedError("Failed to set night vision mode state"))
        }
    }
}