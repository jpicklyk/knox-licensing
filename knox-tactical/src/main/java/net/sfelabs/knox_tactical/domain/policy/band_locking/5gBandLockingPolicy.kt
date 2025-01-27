package net.sfelabs.knox_tactical.domain.policy.band_locking

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.PolicyDefinition
import net.sfelabs.core.knox.feature.api.ConfigurableStatePolicy
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.PolicyParameters
import net.sfelabs.knox_tactical.data.dto.BandLockingDto
import net.sfelabs.knox_tactical.domain.policy.band_locking.BandLockingConfiguration.Companion.NO_BAND_LOCK
import net.sfelabs.knox_tactical.domain.use_cases.radio.Disable5gBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Enable5gBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Get5gBandLockingUseCase

data class BandLocking5gParameters(
    val simSlotId: Int? = null
) : PolicyParameters

@PolicyDefinition(
    title = "5G Band Locking",
    description = "Configure 5G band locking settings per SIM slot or globally.",
    category = PolicyCategory.ConfigurableToggle,
)
class BandLocking5gPolicy:
    ConfigurableStatePolicy<BandLockingState, BandLockingDto, BandLockingConfiguration>() {
    private val getUseCase = Get5gBandLockingUseCase()
    private val enableUseCase = Enable5gBandLockingUseCase()
    private val disableUseCase = Disable5gBandLockingUseCase()
    override val configuration = BandLockingConfiguration()

    override val defaultValue = BandLockingState(
        isEnabled = false,
        band = NO_BAND_LOCK,
        simSlotId = null
    )

    override suspend fun getState(parameters: PolicyParameters): BandLockingState {
        val simSlotId = (parameters as? BandLocking5gParameters)?.simSlotId

        return when (val result = getUseCase(simSlotId)) {
            is ApiResult.Success -> configuration.fromApiData(result.data)
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
        val dto = configuration.toApiData(state)
        return if (dto.band == NO_BAND_LOCK) {
            disableUseCase(dto.simSlotId)
        } else {
            enableUseCase(dto)
        }
    }
}
