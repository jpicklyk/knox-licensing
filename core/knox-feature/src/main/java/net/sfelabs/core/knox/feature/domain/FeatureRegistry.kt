package net.sfelabs.core.knox.feature.domain

import net.sfelabs.core.knox.feature.domain.model.Feature
import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import net.sfelabs.core.knox.feature.domain.model.FeatureHandler
import net.sfelabs.core.knox.feature.domain.model.FeatureKey

interface FeatureRegistry {
    fun <T : Any> getHandler(key: FeatureKey<T>): FeatureHandler<T>?
    fun getFeatures(category: FeatureCategory): List<Feature<*>>
    fun isRegistered(key: FeatureKey<*>): Boolean
}