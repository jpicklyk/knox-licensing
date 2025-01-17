package net.sfelabs.core.knox.feature.hilt

import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureComponent
import net.sfelabs.core.knox.feature.api.FeatureKey
import net.sfelabs.core.knox.feature.api.PolicyState
import net.sfelabs.core.knox.feature.data.repository.DefaultFeatureRegistry
import net.sfelabs.core.knox.feature.domain.registry.FeatureRegistry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HiltFeatureRegistry @Inject constructor() : FeatureRegistry {
    init {
        println("HiltFeatureRegistry being constructed")
    }

    private val delegate = DefaultFeatureRegistry()

    @Inject
    fun setComponents(components: Set<@JvmSuppressWildcards FeatureComponent<out PolicyState>>) {
        println("Injecting components: ${components.size}")
        components.forEach {
            println("Component: ${it.featureName}")
        }
        delegate.components = components
    }

    override fun <T : PolicyState> getHandler(key: FeatureKey<T>) = delegate.getHandler(key)

    override suspend fun getAllFeatures() = delegate.getAllFeatures()

    override suspend fun getFeatures(category: FeatureCategory) = delegate.getFeatures(category)

    override fun isRegistered(key: FeatureKey<*>) = delegate.isRegistered(key)

    override fun getComponent(key: FeatureKey<*>) = delegate.getComponent(key)

    override suspend fun getFeature(featureName: String) = delegate.getFeature(featureName)
}