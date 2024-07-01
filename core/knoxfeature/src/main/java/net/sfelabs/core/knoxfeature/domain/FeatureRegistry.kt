package net.sfelabs.core.knoxfeature.domain

import net.sfelabs.core.knoxfeature.FeatureKey
import javax.inject.Inject

enum class FeatureCategory {
    PRODUCTION,
    EXPERIMENTAL
}

class FeatureRegistry @Inject constructor() {
    private val _features = mutableMapOf<FeatureCategory, MutableSet<FeatureKey<*>>>()

    fun registerFeature(feature: FeatureKey<*>, category: FeatureCategory = FeatureCategory.PRODUCTION) {
        _features.getOrPut(category) { mutableSetOf() }.add(feature)
    }

    fun getFeatures(category: FeatureCategory? = null): Set<FeatureKey<*>> {
        return if (category != null) {
            _features[category]?.toSet() ?: emptySet()
        } else {
            _features.values.flatten().toSet()
        }
    }

    fun getCategorizedFeatures(): Map<FeatureCategory, Set<FeatureKey<*>>> {
        return _features.mapValues { it.value.toSet() }
    }
}