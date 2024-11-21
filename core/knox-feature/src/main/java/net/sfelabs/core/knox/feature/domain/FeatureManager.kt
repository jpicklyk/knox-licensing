package net.sfelabs.core.knox.feature.domain

import net.sfelabs.core.knox.feature.domain.model.Feature
import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import net.sfelabs.core.knox.feature.domain.model.FeatureHandler
import net.sfelabs.core.knox.feature.domain.model.FeatureKey

interface FeatureManager {
    /**
     * Get a feature handler by its key
     */
    fun <T> getFeature(key: FeatureKey<T>): FeatureHandler<T>?

    /**
     * Get all features in a specific category
     */
    fun getFeatures(category: FeatureCategory): List<Feature<*>>

    /**
     * Check if a feature is registered
     */
    fun isFeatureRegistered(key: FeatureKey<*>): Boolean
}