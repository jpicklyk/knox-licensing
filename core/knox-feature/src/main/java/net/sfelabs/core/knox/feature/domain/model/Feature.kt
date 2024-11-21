package net.sfelabs.core.knox.feature.domain.model

data class Feature<out T> (
    val key: FeatureKey<T>,
    val state: FeatureState<T>
)