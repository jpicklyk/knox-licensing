package net.sfelabs.core.knox.feature.data.repository

import net.sfelabs.core.knox.feature.domain.usecase.handler.FeatureHandler
import net.sfelabs.core.knox.feature.domain.model.Feature
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureComponent
import net.sfelabs.core.knox.feature.api.FeatureKey
import net.sfelabs.core.knox.feature.api.PolicyState
import net.sfelabs.core.knox.feature.api.PolicyStateWrapper
import net.sfelabs.core.knox.feature.domain.registry.FeatureRegistry

class DefaultFeatureRegistry : FeatureRegistry {
    var components: Set<FeatureComponent<out PolicyState>> = emptySet()

    private val componentsByName: Map<String, FeatureComponent<out PolicyState>> by lazy {
        components.associateBy { it.featureName }
    }

    override fun getComponent(key: FeatureKey<*>): FeatureComponent<out PolicyState>? {
        return componentsByName[key.featureName]
    }

    override fun <T : PolicyState> getHandler(key: FeatureKey<T>): FeatureHandler<T>? {
        val component = componentsByName[key.featureName] ?: return null

        return if (component.key::class == key::class) {
            @Suppress("UNCHECKED_CAST")
            component.handler as? FeatureHandler<T>
        } else {
            null
        }
    }

    override suspend fun getAllFeatures(): List<Feature<*>> {
        return components.map { component ->
            @Suppress("UNCHECKED_CAST")
            val typedComponent = component
            val handler = typedComponent.handler
            Feature(
                key = typedComponent.key,
                state = PolicyStateWrapper(handler.getState())
            )
        }
    }

    override suspend fun getFeatures(category: FeatureCategory): List<Feature<*>> {
        return components
            .filter { it.category == category }
            .map { component ->
                @Suppress("UNCHECKED_CAST")
                val typedComponent = component
                val handler = typedComponent.handler
                Feature(
                    key = typedComponent.key,
                    state = PolicyStateWrapper(handler.getState())
                )
            }
    }

    override fun isRegistered(key: FeatureKey<*>): Boolean {
        return componentsByName.containsKey(key.featureName)
    }

    override suspend fun getFeature(featureName: String): Feature<PolicyState>? {
        val component = componentsByName[featureName] ?: return null
        @Suppress("UNCHECKED_CAST")
        val handler = component.handler
        return Feature(
            key = component.key,
            state = PolicyStateWrapper(handler.getState())
        )
    }
}