package net.sfelabs.core.knoxfeature

import net.sfelabs.core.knoxfeature.model.FeatureState

data class Feature<out T> (
    val key: FeatureKey<T>,
    val state: FeatureState<T>
)