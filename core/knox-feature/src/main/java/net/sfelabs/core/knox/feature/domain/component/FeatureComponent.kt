package net.sfelabs.core.knox.feature.domain.component

import net.sfelabs.core.knox.feature.domain.handler.FeatureHandler
import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import net.sfelabs.core.knox.feature.domain.model.FeatureKey

interface FeatureComponent<T : Any> {
    val featureName: String
    val description: String
    val category: FeatureCategory
    val handler: FeatureHandler<T>
    val defaultValue: T
    val key: FeatureKey<T>
}