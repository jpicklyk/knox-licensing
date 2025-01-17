package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.domain.usecase.model.ApiError

data class BooleanPolicyState(
    override val isEnabled: Boolean,
    override val isSupported: Boolean = true,
    override val error: ApiError? = null,
    override val exception: Throwable? = null
): PolicyState
