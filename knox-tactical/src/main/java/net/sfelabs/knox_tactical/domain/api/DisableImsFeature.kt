package net.sfelabs.knox_tactical.domain.api

import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.api.domain.model.map
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.internal.component.StateMapping
import net.sfelabs.knox_tactical.domain.model.ImsState
import net.sfelabs.knox_tactical.domain.use_cases.ims.IsImsEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ims.SetImsEnabled

data class ImsParameters(
    val feature: Int = 1,
    val simSlotId: Int = 0
) : FeatureParameters

@FeatureDefinition(
    title = "Disable Modem IMS",
    description = "Disables the cellular modem IMS capability",
    category = FeatureCategory.ConfigurableToggle,
    stateMapping = StateMapping.INVERTED,
)

class DisableImsFeature: FeatureContract<ImsState> {
    override val defaultValue = ImsState(
        isEnabled = false,
        feature = 1,
        simSlotId = 0
    )

    private val getUseCase = IsImsEnabledUseCase()
    private val setUseCase = SetImsEnabled()

    override suspend fun getState(parameters: FeatureParameters): ApiResult<ImsState> {
        val (feature, simSlotId) = when (parameters) {
            is ImsParameters -> parameters.feature to parameters.simSlotId
            FeatureParameters.None -> defaultValue.feature to defaultValue.simSlotId
            else -> throw IllegalArgumentException("Unsupported parameters type: ${parameters.javaClass.simpleName}")
        }
        return getUseCase(feature, simSlotId).map { isEnabled ->
            ImsState(
                isEnabled = isEnabled,
                feature = feature,
                simSlotId = simSlotId
            )
        }
    }

    override suspend fun setState(state: ImsState): ApiResult<Unit> {
        return setUseCase(state)
    }
}
