package net.sfelabs.core.knox.feature.internal.registry

import net.sfelabs.core.knox.feature.internal.model.Feature
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.internal.handler.FeatureHandler
import net.sfelabs.core.knox.feature.internal.component.FeatureComponent
import net.sfelabs.core.knox.feature.api.FeatureKey

interface FeatureRegistry {
    fun <T : Any> getHandler(key: FeatureKey<T>): FeatureHandler<T>?
    suspend fun getFeatures(category: FeatureCategory): List<Feature<*>>
    fun isRegistered(key: FeatureKey<*>): Boolean
    fun getComponent(key: FeatureKey<*>): FeatureComponent<*>?
}