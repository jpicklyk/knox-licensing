package net.sfelabs.core.knox.feature.internal.model

import net.sfelabs.core.knox.feature.api.FeatureKey

data class Feature<out T> (
    val key: FeatureKey<T>,
    val state: FeatureState<T>
)