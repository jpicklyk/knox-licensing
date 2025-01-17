package net.sfelabs.knox_tactical.domain.policy.auto_call_pickup

import net.sfelabs.core.domain.usecase.model.ApiError
import net.sfelabs.core.knox.feature.api.PolicyState
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode

data class AutoCallPickupState(
    override val isEnabled: Boolean,
    override val isSupported: Boolean = true,
    override val error: ApiError? = null,
    override val exception: Throwable? = null,
    val mode: AutoCallPickupMode
): PolicyState {
    override fun withError(error: ApiError?, exception: Throwable?): PolicyState {
        return copy(error = error, exception = exception)
    }
}