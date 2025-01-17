package net.sfelabs.core.knox.feature.domain.model

import net.sfelabs.core.knox.feature.api.FeatureKey
import net.sfelabs.core.knox.feature.api.PolicyState
import net.sfelabs.core.knox.feature.api.PolicyStateWrapper

data class Feature<out T : PolicyState> (
    val key: FeatureKey<T>,
    val state: PolicyStateWrapper<T>
)