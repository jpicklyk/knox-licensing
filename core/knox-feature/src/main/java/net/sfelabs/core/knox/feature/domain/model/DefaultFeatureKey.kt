package net.sfelabs.core.knox.feature.domain.model

data class DefaultFeatureKey<T: Any>(
    override val featureName: String
) : FeatureKey<T>
