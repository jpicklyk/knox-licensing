package net.sfelabs.knox_tactical.domain.use_cases.screen

import kotlinx.coroutines.flow.first
import net.sfelabs.core.domain.repository.PreferencesRepository
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase

class GetNightVisionRedOverlayUseCase (
    private val preferenceRepository: PreferencesRepository = PreferencesRepository.getInstance()
) : CoroutineApiUseCase<Unit, Boolean>() {
    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        val result = preferenceRepository.getValue("night_vision_red_overlay_enabled", false)
        return ApiResult.Success(result.first())
    }

}