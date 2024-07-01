package net.sfelabs.core.knoxfeature

import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.knoxfeature.domain.FeatureHandler
import net.sfelabs.core.knoxfeature.model.FeatureState


enum class TestFeatureKey(override val featureName: String) :
    FeatureKey<Boolean> {
    TestFeature1("test_feature_1"),
    TestFeature2("test_feature_2")
}

class TestFeatureHandler : FeatureHandler<Boolean> {
    override suspend fun getState(): ApiResult<FeatureState<Boolean>> =
        ApiResult.Success(FeatureState(enabled = true, value = true))

    override suspend fun setState(newState: FeatureState<Boolean>): ApiResult<Unit> =
        ApiResult.Success(Unit)
}