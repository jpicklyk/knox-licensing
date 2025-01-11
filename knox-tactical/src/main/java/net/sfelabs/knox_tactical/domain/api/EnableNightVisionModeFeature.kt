package net.sfelabs.knox_tactical.domain.api

import net.sfelabs.core.domain.repository.PreferencesRepository
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.internal.component.StateMapping
import net.sfelabs.knox_tactical.domain.model.NightVisionState
import net.sfelabs.knox_tactical.domain.use_cases.screen.GetNightVisionModeStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.screen.SetNightVisionModeStateUseCase

@FeatureDefinition(
    title = "Enable Night Vision Mode",
    description = "Enables the night vision mode capability",
    category = FeatureCategory.ConfigurableToggle,
    stateMapping = StateMapping.DIRECT,
)
class EnableNightVisionModeFeature(
    preferenceRepository: PreferencesRepository = PreferencesRepository.getInstance()
): FeatureContract<NightVisionState> {
    override val defaultValue = NightVisionState(
        isEnabled = false,
        useRedOverlay = false
    )

    private val getUseCase = GetNightVisionModeStateUseCase(preferenceRepository)
    private val setUseCase = SetNightVisionModeStateUseCase(preferenceRepository)

    override suspend fun getState(parameters: FeatureParameters): ApiResult<NightVisionState> {
        return getUseCase()
    }

    override suspend fun setState(state: NightVisionState): ApiResult<Unit> {
        return setUseCase(state)
    }
}