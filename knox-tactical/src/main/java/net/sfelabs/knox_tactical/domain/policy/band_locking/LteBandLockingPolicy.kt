package net.sfelabs.knox_tactical.domain.policy.band_locking

import com.samsung.android.knox.custom.CustomDeviceManager.BANDLOCK_NONE
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.PolicyDefinition
import net.sfelabs.core.knox.feature.api.ConfigurableStatePolicy
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.PolicyParameters
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.radio.DisableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.EnableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.GetBandLockingStateUseCase

data class BandLockingParameters(
    val simSlotId: Int? = null
) : PolicyParameters

@PolicyDefinition(
    title = "LTE Band Locking",
    description = "Configure LTE band locking settings per SIM slot or globally.",
    category = PolicyCategory.ConfigurableToggle
)
class LteBandLockingPolicy :
    ConfigurableStatePolicy<BandLockingState, BandLockingState, BandLockingConfiguration>(
        stateMapping = StateMapping.DIRECT
    ) {

    private val getUseCase = GetBandLockingStateUseCase()
    private val enableUseCase = EnableBandLockingUseCase()
    private val disableUseCase = DisableBandLockingUseCase()
    override val configuration = BandLockingConfiguration()

    override val defaultValue = BandLockingState(
        isEnabled = false,
        band = BANDLOCK_NONE
    )

    override suspend fun getState(parameters: PolicyParameters): BandLockingState {
        val simSlotId = (parameters as? BandLockingParameters)?.simSlotId

        return when (val result = getUseCase(simSlotId)) {
            is ApiResult.Success -> configuration.fromApiData(
                defaultValue.copy(
                    band = result.data,
                    simSlotId = simSlotId
                )
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

    override suspend fun setState(state: BandLockingState): ApiResult<Unit> {
        val config = configuration.toApiData(state)
        return if (!config.isEnabled) {
            disableUseCase(config.simSlotId)
        } else {
            enableUseCase(config.band, config.simSlotId)
        }
    }

}