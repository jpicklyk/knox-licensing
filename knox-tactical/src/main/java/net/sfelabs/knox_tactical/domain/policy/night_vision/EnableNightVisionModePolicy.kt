package net.sfelabs.knox_tactical.domain.policy.night_vision

import net.sfelabs.core.domain.repository.PreferencesRepository
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.ConfigurableStatePolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.screen.GetNightVisionModeStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.screen.SetNightVisionModeStateUseCase

@FeatureDefinition(
    title = "Enable Night Vision Mode",
    description = "Enables the night vision mode capability",
    category = FeatureCategory.ConfigurableToggle
)
class EnableNightVisionModePolicy(
    preferenceRepository: PreferencesRepository = PreferencesRepository.getInstance()
) : ConfigurableStatePolicy<NightVisionState, NightVisionConfiguration>(
    stateMapping = StateMapping.DIRECT
) {
    private val getUseCase = GetNightVisionModeStateUseCase(preferenceRepository)
    private val setUseCase = SetNightVisionModeStateUseCase(preferenceRepository)

    override val defaultValue = NightVisionState(
        isEnabled = false,
        useRedOverlay = false
    )

    override suspend fun getState(parameters: FeatureParameters): NightVisionState {
        return when (val result = getUseCase()) {
            is ApiResult.Success -> NightVisionConfiguration(
                enabled = result.data.isEnabled,
                useRedOverlay = result.data.useRedOverlay,
                stateMapping = stateMapping
            ).toState(defaultValue)
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
        val config = toConfiguration(state)
        return setUseCase(NightVisionState(
            isEnabled = config.enabled,
            useRedOverlay = config.useRedOverlay
        ))
    }

    override fun toConfiguration(state: NightVisionState): NightVisionConfiguration =
        NightVisionConfiguration(
            enabled = state.isEnabled,
            useRedOverlay = state.useRedOverlay,
            stateMapping = stateMapping
        )
}