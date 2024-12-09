package net.sfelabs.core.knox.feature.domain.model

import net.sfelabs.core.knox.feature.domain.handler.FeatureHandler

interface FeatureComponent<T : Any> {
    val featureName: String
    val description: String
    val category: FeatureCategory
    val handler: FeatureHandler<T>
    val defaultValue: T
    val key: FeatureKey<T>
}