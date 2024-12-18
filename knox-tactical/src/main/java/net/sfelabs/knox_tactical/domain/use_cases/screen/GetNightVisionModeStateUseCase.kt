package net.sfelabs.knox_tactical.domain.use_cases.screen

import net.sfelabs.core.domain.repository.PreferencesRepository
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.core.knox.api.domain.executeForSuccess
import net.sfelabs.core.knox.api.domain.useCaseBlock
import net.sfelabs.knox_tactical.domain.model.NightVisionState

class GetNightVisionModeStateUseCase(
    private val preferenceRepository: PreferencesRepository = PreferencesRepository.getInstance()
): CoroutineApiUseCase<Unit, NightVisionState>() {

    override suspend fun execute(params: Unit): ApiResult<NightVisionState> = useCaseBlock {
        val isEnabled = executeForSuccess { GetNightVisionModeUseCase().invoke(Unit) }
        val useRedOverlay = executeForSuccess {
            GetNightVisionRedOverlayUseCase(preferenceRepository).invoke(Unit)
        }

        if (isEnabled != null && useRedOverlay != null) {
            ApiResult.Success(NightVisionState(
                isEnabled = isEnabled,
                useRedOverlay = useRedOverlay
            ))
        } else {
            errors().firstOrNull() ?: ApiResult.Error(DefaultApiError.UnexpectedError())
        }
    }.getResults().filterIsInstance<ApiResult<NightVisionState>>().first()
}