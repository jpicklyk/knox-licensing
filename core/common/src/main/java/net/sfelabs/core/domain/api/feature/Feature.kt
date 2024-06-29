package net.sfelabs.core.domain.api.feature

data class Feature<out T> (
    val key: FeatureKey<T>,
    val state: FeatureState<T>
)