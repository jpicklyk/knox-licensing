package net.sfelabs.knox_tactical.domain.policy.band_locking

import com.samsung.android.knox.custom.CustomDeviceManager.BANDLOCK_NONE
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.api.StateMapping
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
    stateMapping = StateMapping.DIRECT
)
class BandLocking5gPolicy : FeatureContract<BandLockingState> {
    private val getUseCase = Get5gBandLockingUseCase()
    private val enableUseCase = Enable5gBandLockingUseCase()
    private val disableUseCase = Disable5gBandLockingUseCase()

    override val defaultValue = BandLockingState(
        isEnabled = false,
        band = 0
    )

    override suspend fun getState(parameters: FeatureParameters): BandLockingState {
        val simSlotId = when (parameters) {
            is BandLocking5gParameters -> parameters.simSlotId
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
