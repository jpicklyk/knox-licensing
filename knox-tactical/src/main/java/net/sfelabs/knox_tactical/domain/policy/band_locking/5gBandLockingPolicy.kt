package net.sfelabs.knox_tactical.domain.policy.band_locking

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.ConfigurablePolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.knox_tactical.domain.policy.band_locking.BandLockingConfiguration.Companion.NO_BAND_LOCK
import net.sfelabs.knox_tactical.domain.use_cases.radio.Disable5gBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Enable5gBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Get5gBandLockingUseCase

data class BandLocking5gParameters(
    val simSlotId: Int? = null
) : FeatureParameters

@FeatureDefinition(
    title = "5G Band Locking",
    description = "Configure 5G band locking settings per SIM slot or globally.",
    category = FeatureCategory.ConfigurableToggle,
)
class BandLocking5gPolicy :
    FeatureContract<BandLockingState>,
    ConfigurablePolicy<BandLockingState, BandLockingConfiguration> {
    private val getUseCase = Get5gBandLockingUseCase()
    private val enableUseCase = Enable5gBandLockingUseCase()
    private val disableUseCase = Disable5gBandLockingUseCase()

    override val defaultValue = BandLockingState(
        isEnabled = false,
        band = NO_BAND_LOCK,
    )

    override suspend fun getState(parameters: FeatureParameters): BandLockingState {
        val simSlotId = (parameters as? BandLocking5gParameters)?.simSlotId

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
        return if (config.band == NO_BAND_LOCK) {
            disableUseCase(config.simSlotId)
        } else {
            enableUseCase(config.band, config.simSlotId)
        }
    }

    override fun toConfiguration(state: BandLockingState): BandLockingConfiguration =
        BandLockingConfiguration(
            band = state.band,
            simSlotId = state.simSlotId
        )

}
