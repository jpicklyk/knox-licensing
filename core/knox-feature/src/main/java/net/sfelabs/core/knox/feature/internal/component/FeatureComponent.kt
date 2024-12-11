package net.sfelabs.core.knox.feature.internal.component

import net.sfelabs.core.knox.feature.internal.handler.FeatureHandler
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureKey

interface FeatureComponent<T : Any> {
    val featureName: String
    val title: String
    val description: String
    val category: FeatureCategory
    val handler: FeatureHandler<T>
    val defaultValue: T
    val key: FeatureKey<T>
}