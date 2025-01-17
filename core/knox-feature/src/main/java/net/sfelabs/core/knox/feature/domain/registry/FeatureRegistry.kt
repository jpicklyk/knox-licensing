package net.sfelabs.core.knox.feature.domain.registry

import net.sfelabs.core.knox.feature.domain.model.Feature
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.domain.usecase.handler.FeatureHandler
import net.sfelabs.core.knox.feature.api.FeatureComponent
import net.sfelabs.core.knox.feature.api.FeatureKey
import net.sfelabs.core.knox.feature.api.PolicyState

interface FeatureRegistry {
    fun <T : PolicyState> getHandler(key: FeatureKey<T>): FeatureHandler<T>?
    suspend fun getAllFeatures(): List<Feature<PolicyState>>
    suspend fun getFeatures(category: FeatureCategory): List<Feature<PolicyState>>
    fun isRegistered(key: FeatureKey<*>): Boolean
    fun getComponent(key: FeatureKey<*>): FeatureComponent<out PolicyState>?
    suspend fun getFeature(featureName: String): Feature<PolicyState>?
}