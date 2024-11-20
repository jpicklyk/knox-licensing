package net.sfelabs.core.knoxfeature.domain.model

data class Feature<out T> (
    val key: FeatureKey<T>,
    val state: FeatureState<T>
)