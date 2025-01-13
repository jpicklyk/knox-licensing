package net.sfelabs.knox_tactical.domain.use_cases.screen

import net.sfelabs.core.domain.repository.PreferencesRepository
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.executor.UseCaseBuilder
import net.sfelabs.core.domain.usecase.executor.combine
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.domain.model.NightVisionState

class GetNightVisionModeStateUseCase(
    private val preferenceRepository: PreferencesRepository = PreferencesRepository.getInstance()
): SuspendingUseCase<Unit, NightVisionState>() {

    override suspend fun execute(params: Unit): ApiResult<NightVisionState> {
        return UseCaseBuilder()
            .parallel {
                GetNightVisionModeUseCase().invoke()
            }
            .add {
                GetNightVisionRedOverlayUseCase(preferenceRepository).invoke()
            }
            .execute()
            .combine<Boolean, NightVisionState> { results ->
                NightVisionState(
                    isEnabled = results[0],
                    useRedOverlay = results[1]
                )
            }
    }
}