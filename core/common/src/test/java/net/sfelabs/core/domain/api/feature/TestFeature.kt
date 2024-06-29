package net.sfelabs.core.domain.api.feature

import net.sfelabs.core.domain.api.ApiResult


enum class TestFeatureKey(override val featureName: String) : FeatureKey<Boolean> {
    TestFeature1("test_feature_1"),
    TestFeature2("test_feature_2")
}

class TestFeatureHandler : FeatureHandler<Boolean> {
    override suspend fun getState(): ApiResult<FeatureState<Boolean>> =
        ApiResult.Success(FeatureState(enabled = true, value = true))

    override suspend fun setState(newState: FeatureState<Boolean>): ApiResult<Unit> =
        ApiResult.Success(Unit)
}