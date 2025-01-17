package net.sfelabs.knox_tactical.domain.model

import net.sfelabs.core.domain.usecase.model.ApiError
import net.sfelabs.core.knox.feature.api.PolicyState

data class ImsState(
    override val isEnabled: Boolean,
    override val isSupported: Boolean = true,
    override val error: ApiError? = null,
    override val exception: Throwable? = null,
    val simSlotId: Int = 0,
    val feature: Int = 1
): PolicyState
