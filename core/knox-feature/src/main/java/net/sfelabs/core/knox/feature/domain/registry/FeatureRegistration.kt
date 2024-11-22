package net.sfelabs.core.knox.feature.domain.registry

import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import net.sfelabs.core.knox.feature.domain.handler.FeatureHandler
import net.sfelabs.core.knox.feature.domain.model.FeatureKey

interface FeatureRegistration<T : Any> {
    val key: FeatureKey<T>
    val handler: FeatureHandler<T>
    val category: FeatureCategory
}