package net.sfelabs.core.knox.feature.domain.usecase.handler

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.api.PolicyParameters
import net.sfelabs.core.knox.feature.api.PolicyState

interface PolicyHandler<T : PolicyState> {
    suspend fun getState(parameters: PolicyParameters = PolicyParameters.None): T
    suspend fun setState(newState: T): ApiResult<Unit>
}