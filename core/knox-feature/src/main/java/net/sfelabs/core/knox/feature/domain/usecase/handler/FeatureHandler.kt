package net.sfelabs.core.knox.feature.domain.usecase.handler

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.api.PolicyState

interface FeatureHandler<T : PolicyState> {
    suspend fun getState(parameters: FeatureParameters = FeatureParameters.None): T
    suspend fun setState(newState: T): ApiResult<Unit>
}