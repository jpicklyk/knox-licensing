package net.sfelabs.core.knox.feature.domain.registry

import kotlinx.coroutines.runBlocking
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.feature.domain.handler.FeatureHandler
import net.sfelabs.core.knox.feature.domain.model.Feature
import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import net.sfelabs.core.knox.feature.domain.model.FeatureImplementation
import net.sfelabs.core.knox.feature.domain.model.FeatureKey

class DefaultFeatureRegistry : FeatureRegistry {
    private val registrations = mutableMapOf<String, FeatureRegistration<*>>()

    fun register(registration: FeatureRegistration<*>) {
        registrations[registration.key.featureName] = registration
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getHandler(key: FeatureKey<T>): FeatureHandler<T>? {
        val registration = registrations[key.featureName] ?: return null
        // Add type check before casting
        return if (registration.key::class == key::class) {
            registration.handler as? FeatureHandler<T>
        } else {
            null
        }
    }

    override fun <T : Any> getRegistration(key: FeatureKey<T>): FeatureRegistration<T>? {
        @Suppress("UNCHECKED_CAST")
        return registrations[key.featureName] as? FeatureRegistration<T>
    }

    override fun getFeatures(category: FeatureCategory): List<Feature<*>> {
        return registrations.values
            .filter { it.category == category }
            .mapNotNull { registration ->
                runBlocking {
                    when (val stateResult = registration.handler.getState()) {
                        is ApiResult.Success -> Feature(registration.key, stateResult.data)
                        else -> null
                    }
                }
            }
    }

    override fun isRegistered(key: FeatureKey<*>): Boolean {
        return registrations.containsKey(key.featureName)
    }

    private fun createRegistration(impl: FeatureImplementation<*>): FeatureRegistration<*> {
        val featureKey = impl.
        val featureCategory = impl.category
        val featureHandler = impl

        return FeatureRegistration(
            key = featureKey,
            category = featureCategory,
            handler = featureHandler
        )
    }
}