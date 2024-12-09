package net.sfelabs.core.knox.feature.domain.registry

import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.feature.domain.handler.FeatureHandler
import net.sfelabs.core.knox.feature.domain.model.Feature
import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import net.sfelabs.core.knox.feature.domain.model.FeatureComponent
import net.sfelabs.core.knox.feature.domain.model.FeatureKey
import net.sfelabs.core.knox.feature.domain.model.FeatureState

class DefaultFeatureRegistry : FeatureRegistry {
    var components: Set<FeatureComponent<*>> = emptySet()

    private val componentsByName: Map<String, FeatureComponent<*>> by lazy {
        components.associateBy { it.featureName }
    }

    override fun getComponent(key: FeatureKey<*>): FeatureComponent<*>? {
        return componentsByName[key.featureName]
    }

    override fun <T : Any> getHandler(key: FeatureKey<T>): FeatureHandler<T>? {
        val component = componentsByName[key.featureName] ?: return null

        // Check if the component's value type matches the key's type
        return if (component.key::class == key::class) {
            @Suppress("UNCHECKED_CAST")
            component.handler as? FeatureHandler<T>
        } else {
            null
        }
    }

    override suspend fun getFeatures(category: FeatureCategory): List<Feature<*>> {
        return components
            .filter { it.category == category }
            .map { component ->
                @Suppress("UNCHECKED_CAST")
                val handler = component.handler as FeatureHandler<Any>
                val state = when (val result = handler.getState()) {
                    is ApiResult.Success -> result.data
                    is ApiResult.Error -> FeatureState(
                        enabled = false,
                        value = component.defaultValue
                    )
                    is ApiResult.NotSupported -> FeatureState(
                        enabled = false,
                        value = component.defaultValue
                    )
                }

                Feature(
                    key = component.key,
                    state = state
                )
            }
    }

    override fun isRegistered(key: FeatureKey<*>): Boolean {
        return componentsByName.containsKey(key.featureName)
    }
}