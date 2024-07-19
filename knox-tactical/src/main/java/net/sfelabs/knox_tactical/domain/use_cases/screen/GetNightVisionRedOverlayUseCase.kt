package net.sfelabs.knox_tactical.domain.use_cases.screen

import kotlinx.coroutines.flow.first
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.use_case.CoroutineApiUseCase
import net.sfelabs.core.domain.use_case.GetPreferenceUseCase
import javax.inject.Inject

class GetNightVisionRedOverlayUseCase @Inject constructor(
    private val getPreferenceUseCase: GetPreferenceUseCase<Boolean>
) : CoroutineApiUseCase<Unit, Boolean>() {
    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        val result = getPreferenceUseCase("night_vision_red_overlay_enabled", false)
        return ApiResult.Success(result.first())
    }

}