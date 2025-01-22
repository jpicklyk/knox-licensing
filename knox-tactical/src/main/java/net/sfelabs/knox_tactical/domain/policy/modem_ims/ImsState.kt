package net.sfelabs.knox_tactical.domain.policy.modem_ims

import net.sfelabs.core.domain.usecase.model.ApiError
import net.sfelabs.core.knox.feature.api.PolicyState

data class ImsState(
    override val isEnabled: Boolean,
    override val isSupported: Boolean = true,
    override val error: ApiError? = null,
    override val exception: Throwable? = null,
    val simSlotId: Int = 0,
): PolicyState {
    override fun withEnabled(enabled: Boolean): PolicyState {
        return copy(isEnabled = enabled)
    }

    override fun withError(error: ApiError?, exception: Throwable?): PolicyState {
        return copy(error = error, exception = exception)
    }
}
