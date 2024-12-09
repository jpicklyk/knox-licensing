package net.sfelabs.core.knox.feature.hilt

import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import net.sfelabs.core.knox.feature.domain.model.FeatureComponent
import net.sfelabs.core.knox.feature.domain.model.FeatureKey
import net.sfelabs.core.knox.feature.domain.registry.DefaultFeatureRegistry
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
    fun setComponents(components: Set<@JvmSuppressWildcards FeatureComponent<*>>) {
        println("Injecting components: ${components.size}")
        components.forEach {
            println("Component: ${it.featureName}")
        }
        delegate.components = components
    }

    override fun <T : Any> getHandler(key: FeatureKey<T>) = delegate.getHandler(key)

    override suspend fun getFeatures(category: FeatureCategory) = delegate.getFeatures(category)

    override fun isRegistered(key: FeatureKey<*>) = delegate.isRegistered(key)

    override fun getComponent(key: FeatureKey<*>) = delegate.getComponent(key)
}