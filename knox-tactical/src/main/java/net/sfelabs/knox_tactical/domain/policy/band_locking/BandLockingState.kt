package net.sfelabs.knox_tactical.domain.policy.band_locking

import net.sfelabs.core.domain.usecase.model.ApiError
import net.sfelabs.core.knox.feature.api.PolicyState

data class BandLockingState(
    override val isEnabled: Boolean,
    override val isSupported: Boolean = true,
    override val error: ApiError? = null,
    override val exception: Throwable? = null,
    val band: Int,
    val simSlotId: Int? = null
): PolicyState {
    override fun withEnabled(enabled: Boolean): PolicyState {
        return copy(isEnabled = enabled)
    }

    override fun withError(error: ApiError?, exception: Throwable?): PolicyState {
        return copy(error = error, exception = exception)
    }
}
