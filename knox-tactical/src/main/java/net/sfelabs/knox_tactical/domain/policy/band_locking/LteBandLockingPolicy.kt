package net.sfelabs.knox_tactical.domain.policy.band_locking

import com.samsung.android.knox.custom.CustomDeviceManager.BANDLOCK_NONE
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.ConfigurableStatePolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.radio.DisableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.EnableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.GetBandLockingStateUseCase

data class BandLockingParameters(
    val simSlotId: Int? = null
) : FeatureParameters

@FeatureDefinition(
    title = "LTE Band Locking",
    description = "Configure LTE band locking settings per SIM slot or globally.",
    category = FeatureCategory.ConfigurableToggle
)
class LteBandLockingPolicy :
    ConfigurableStatePolicy<BandLockingState, BandLockingConfiguration>(
        stateMapping = StateMapping.DIRECT
    ) {

    private val getUseCase = GetBandLockingStateUseCase()
    private val enableUseCase = EnableBandLockingUseCase()
    private val disableUseCase = DisableBandLockingUseCase()

    override val defaultValue = BandLockingState(
        isEnabled = false,
        band = BANDLOCK_NONE
    )

    override suspend fun getState(parameters: FeatureParameters): BandLockingState {
        val simSlotId = (parameters as? BandLockingParameters)?.simSlotId

        return when (val result = getUseCase(simSlotId)) {
            is ApiResult.Success -> BandLockingConfiguration(
                band = result.data,
                simSlotId = simSlotId
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

    override suspend fun setState(state: BandLockingState): ApiResult<Unit> {
        val config = toConfiguration(state)
        return if (config.band == BANDLOCK_NONE) {
            disableUseCase(config.simSlotId)
        } else {
            enableUseCase(config.band, config.simSlotId)
        }
    }

    override fun toConfiguration(state: BandLockingState): BandLockingConfiguration =
        if (!state.isEnabled) {
            BandLockingConfiguration.disabled(state.simSlotId)
        } else {
            BandLockingConfiguration(
                band = state.band,
                simSlotId = state.simSlotId
            )
        }
}