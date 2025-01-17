package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.repository.PreferencesRepository
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.model.NightVisionState
import net.sfelabs.knox_tactical.domain.use_cases.screen.GetNightVisionModeStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.screen.SetNightVisionModeStateUseCase

@FeatureDefinition(
    title = "Enable Night Vision Mode",
    description = "Enables the night vision mode capability",
    category = FeatureCategory.ConfigurableToggle,
    stateMapping = StateMapping.DIRECT,
)
class EnableNightVisionModePolicy(
    preferenceRepository: PreferencesRepository = PreferencesRepository.getInstance()
): FeatureContract<NightVisionState> {
    override val defaultValue = NightVisionState(
        isEnabled = false,
        useRedOverlay = false
    )

    private val getUseCase = GetNightVisionModeStateUseCase(preferenceRepository)
    private val setUseCase = SetNightVisionModeStateUseCase(preferenceRepository)

    override suspend fun getState(parameters: FeatureParameters): NightVisionState {
        return when (val result = getUseCase()) {
            is ApiResult.Success -> defaultValue.copy(
                isEnabled = result.data.isEnabled,
                useRedOverlay = result.data.useRedOverlay
            )
            is ApiResult.NotSupported -> defaultValue.copy(
                isSupported = false
            )
            is ApiResult.Error -> defaultValue.copy(
                error = result.apiError,
                exception = result.exception
            )
        }
    }

    override suspend fun setState(state: NightVisionState): ApiResult<Unit> {
        return setUseCase(state)
    }
}