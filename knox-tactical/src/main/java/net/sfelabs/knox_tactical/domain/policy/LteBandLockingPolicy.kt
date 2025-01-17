package net.sfelabs.knox_tactical.domain.policy

import com.samsung.android.knox.custom.CustomDeviceManager.BANDLOCK_NONE
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.policy.band_locking.BandLocking5gParameters
import net.sfelabs.knox_tactical.domain.policy.band_locking.BandLockingState
import net.sfelabs.knox_tactical.domain.use_cases.radio.DisableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.EnableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.GetBandLockingStateUseCase

data class BandLockingParameters(
    val simSlotId: Int? = null
) : FeatureParameters

@FeatureDefinition(
    title = "LTE Band Locking",
    description = "Configure LTE band locking settings per SIM slot or globally.",
    category = FeatureCategory.ConfigurableToggle,
    stateMapping = StateMapping.DIRECT
)
class LteBandLockingPolicy : FeatureContract<BandLockingState> {
    private val getUseCase = GetBandLockingStateUseCase()
    private val enableUseCase = EnableBandLockingUseCase()
    private val disableUseCase = DisableBandLockingUseCase()

    override val defaultValue = BandLockingState(
        isEnabled = false,
        band = 0
    )

    override suspend fun getState(parameters: FeatureParameters): BandLockingState {
        val simSlotId = when (parameters) {
            is BandLockingParameters -> parameters.simSlotId
            else -> null
        }
        return when (val result = getUseCase(simSlotId)) {
            is ApiResult.Success -> BandLockingState(
                isEnabled = result.data != BANDLOCK_NONE,
                band = result.data,
                simSlotId = simSlotId
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
        return if (!state.isEnabled) {
            disableUseCase(state.simSlotId)
        } else {
            enableUseCase(state.band, state.simSlotId)
        }
    }
}