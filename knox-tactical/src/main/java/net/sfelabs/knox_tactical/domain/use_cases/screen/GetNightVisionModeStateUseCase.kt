package net.sfelabs.knox_tactical.domain.use_cases.screen

import net.sfelabs.core.domain.repository.PreferencesRepository
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.UseCaseExecutor
import net.sfelabs.core.knox.api.domain.SuspendingUseCase
import net.sfelabs.knox_tactical.domain.model.NightVisionState

class GetNightVisionModeStateUseCase(
    private val preferenceRepository: PreferencesRepository = PreferencesRepository.getInstance()
): SuspendingUseCase<Unit, NightVisionState>() {

    override suspend fun execute(params: Unit): ApiResult<NightVisionState> {
        return UseCaseExecutor().executeAndCombine(
            operations = listOf(
                { GetNightVisionModeUseCase().invoke(Unit) },
                { GetNightVisionRedOverlayUseCase(preferenceRepository).invoke(Unit) }
            ),
            type = Boolean::class.java
        ) { results ->
            NightVisionState(
                isEnabled = results[0],
                useRedOverlay = results[1]
            )
        }
    }
}