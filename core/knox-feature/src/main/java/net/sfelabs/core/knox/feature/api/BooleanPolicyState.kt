package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.domain.usecase.model.ApiError

data class BooleanPolicyState(
    override val isEnabled: Boolean,
    override val isSupported: Boolean = true,
    override val error: ApiError? = null,
    override val exception: Throwable? = null
): PolicyState {
    override fun withEnabled(enabled: Boolean): PolicyState {
        return copy(isEnabled = enabled)
    }

    override fun withError(error: ApiError?, exception: Throwable?): PolicyState {
        return copy(error = error, exception = exception)
    }
}
